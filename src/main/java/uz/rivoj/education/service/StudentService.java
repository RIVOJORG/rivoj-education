package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.AttendanceCR;
import uz.rivoj.education.dto.request.ChatCR;
import uz.rivoj.education.dto.request.StudentCR;
import uz.rivoj.education.dto.request.StudentUpdate;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;
import org.springframework.data.domain.Pageable;
import uz.rivoj.education.service.firebase.FirebaseService;
import uz.rivoj.education.service.jwt.JwtUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import static uz.rivoj.education.entity.enums.AttendanceStatus.CHECKED;
import static uz.rivoj.education.entity.enums.AttendanceStatus.UNCHECKED;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentInfoRepository studentInfoRepository;
    private final SubjectRepository subjectRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TeacherInfoRepository teacherInfoRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;
    private final VerificationRepository verificationRepository;
    private final JwtUtil jwtUtil;
    private final FirebaseService firebaseService;



    public ResponseEntity<String> addStudent(StudentCR studentCR) {
        if (userRepository.findByPhoneNumber(studentCR.getPhoneNumber()).isPresent()) throw new DataAlreadyExistsException("Student already exists with this phone number: " + studentCR.getPhoneNumber());

        SubjectEntity subject = subjectRepository.findById(studentCR.getSubjectId())
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id: " + studentCR.getSubjectId()));

        ModuleEntity moduleEntity = moduleRepository.findById(studentCR.getStarterModuleId())
                .orElseThrow(() -> new DataNotFoundException("Module not found with subject id: " + subject.getId()));

        LessonEntity lesson = lessonRepository.findFirstByModule_IdOrderByNumberAsc(moduleEntity.getId())
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with module id: " + moduleEntity.getId()));

        UserEntity userEntity = UserEntity.builder()
                .name(studentCR.getName())
                .password(passwordEncoder.encode(studentCR.getPassword()))
                .phoneNumber(studentCR.getPhoneNumber())
                .role(UserRole.STUDENT)
                .userStatus(UserStatus.UNBLOCK)
                .birthday(studentCR.getBirthday())
                .surname(studentCR.getSurname())
                .build();

        StudentInfo student = StudentInfo.builder()
                .coin(0)
                .student(userEntity)
                .subject(subject)
                .lesson(lesson)
                .currentModule(moduleEntity)
                .totalScore(0)
                .build();
        UserEntity savedUser = userRepository.save(userEntity);
        studentInfoRepository.save(student);
        try {
            firebaseService.createUser(new UserDetailsDTO(String.valueOf(savedUser.getId()),savedUser.getPhoneNumber(),savedUser.getAvatar(),savedUser.getName(),savedUser.getSurname(),String.valueOf(savedUser.getRole())));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create user on Firebase! \n" + e.getMessage());
        }
        Optional<List<UUID>> optionalTeachersId = userRepository.findTeacherIdesIdBySubjectId(UserRole.TEACHER,studentCR.getSubjectId());
        optionalTeachersId.ifPresent(teacherIdes -> teacherIdes.forEach(teacherId -> {
            try {
                firebaseService.createChat(new ChatCR(String.valueOf(teacherId), String.valueOf(savedUser.getId())), String.valueOf(UUID.randomUUID()));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully created!");
    }



    public StudentResponse updateProfile(StudentUpdate studentUpdate, UUID studentId) {
        StudentInfo studentInfo = studentInfoRepository.findByStudentId(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        UserEntity userEntity = userRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        if (studentUpdate.getBirthday() != null) studentInfo.getStudent().setBirthday(studentUpdate.getBirthday());
        if (studentUpdate.getSurname() != null) userEntity.setSurname(studentUpdate.getSurname());
        if (studentUpdate.getName() != null) userEntity.setName(studentUpdate.getName());
        UserEntity save = userRepository.save(userEntity);
        studentInfoRepository.save(studentInfo);
        StudentResponse response = modelMapper.map(userEntity, StudentResponse.class);
        response.setBirth(studentInfo.getStudent().getBirthday());
        response.setSubjectId(studentInfo.getSubject().getId());
        response.setCurrentLessonId(studentInfo.getLesson().getId());
        firebaseService.updateUser(new UserDetailsDTO(String.valueOf(save.getId()),save.getPhoneNumber(),save.getAvatar(),save.getName(),save.getSurname(),String.valueOf(save.getRole())));
        return response;
    }

    public List<StudentResponse> getAllMyStudent(int page, int size, UUID teacherId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentInfo> students = studentInfoRepository.findBySubject_Id(teacherInfo.getSubject().getId(), pageable);
        return students.stream()
                .map(this::convertToStudentResponse)
                .collect(Collectors.toList());
    }


    private StudentResponse convertToStudentResponse(StudentInfo studentInfo) {
        assert studentInfo.getLesson() != null;
        assert studentInfo.getCurrentModule() != null;
        assert studentInfo.getSubject() != null;
        return new StudentResponse(
                studentInfo.getStudent().getId(),
                studentInfo.getStudent().getName(),
                studentInfo.getStudent().getSurname(),
                studentInfo.getStudent().getAvatar(),
                studentInfo.getStudent().getPhoneNumber(),
                studentInfo.getStudent().getBirthday(),
                studentInfo.getSubject().getId(),
                studentInfo.getSubject().getTitle(),
                studentInfo.getLesson().getId(),
                studentInfo.getCurrentModule().getId(),
                studentInfo.getLesson().getNumber(),
                studentInfo.getCurrentModule().getNumber(),
                studentInfo.getCoin(),
                studentInfo.getTotalScore(),
                studentInfo.getStudent().getUserStatus()

        );
    }
    @Transactional
    public String uploadHomework(AttendanceCR attendanceCR, UUID userId,List<MultipartFile> files) {
        StudentInfo student = studentInfoRepository.findByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        LessonEntity lesson = lessonRepository.findById(attendanceCR.getLessonId())
                .orElseThrow(() -> new DataNotFoundException("Lesson not found!"));
        Optional<AttendanceEntity> attendance = attendanceRepository.findByStudentIdAndLessonId(student.getId(), lesson.getId());
        AttendanceEntity attendanceEntity ;
        if (attendance.isPresent() && attendance.get().getAttemptsNumber() < 3) {
            attendance.get().getAnswers().forEach(uploadService::deleteFile);
            attendanceRepository.delete(attendance.get());
            List<String> homeworkPaths = new ArrayList<>();
            for (MultipartFile multipartFile : files) {
                String fileName = user.getName() + "'s_homework";
                try {
                    homeworkPaths.add(uploadService.uploadFile(multipartFile, fileName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            attendanceEntity = AttendanceEntity.builder()
                    .answers(homeworkPaths)
                    .coin(0)
                    .lesson(lesson)
                    .student(student)
                    .attemptsNumber(attendance.get().getAttemptsNumber()+1)
                    .status(UNCHECKED)
                    .build();
            attendanceRepository.save(attendanceEntity);
            return "Uploaded!";
        }
        else if (attendance.isEmpty() ) {
            List<String> homeworkPaths = new ArrayList<>();
            for (MultipartFile multipartFile : files) {
                String fileName = user.getName() + "'s_homework";
                try {
                    homeworkPaths.add(uploadService.uploadFile(multipartFile, fileName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            attendanceEntity = AttendanceEntity.builder()
                    .answers(homeworkPaths)
                    .coin(0)
                    .lesson(lesson)
                    .student(student)
                    .attemptsNumber(1)
                    .status(UNCHECKED)
                    .build();
            attendanceRepository.save(attendanceEntity);
            return "Uploaded!";
        }
        else {
            throw new DataNotFoundException("Attempts more than 3 times you cannot upload!");
        }

    }




    public List<ProgressResponse> getStudentProgress(UUID studentId) {
        StudentInfo studentInfo = studentInfoRepository.findByStudentId(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        List<ModuleEntity> moduleEntities = moduleRepository.findAllBySubject_IdOrderByNumberAsc(studentInfo.getSubject().getId())
                .orElseThrow(() -> new DataNotFoundException("Modules not found!"));
        List<ProgressResponse> progressResponseList = new ArrayList<>();
        moduleEntities.forEach(moduleEntity -> {
            Optional<List<LessonEntity>> lessonEntities = lessonRepository.findAllByModule_IdOrderByNumberAsc(moduleEntity.getId());
            ProgressResponse progressResponse = new ProgressResponse();
            progressResponse.setModuleNumber(moduleEntity.getNumber());
            if (lessonEntities.isPresent() && !lessonEntities.get().isEmpty()) {
                List<LessonEntity> lessons = lessonEntities.get();
                progressResponse.setLessonCount(lessons.size());
                List<Integer> scoreList = new ArrayList<>();
                lessons.forEach(lessonEntity -> {
                    Optional<AttendanceEntity> attendance = attendanceRepository.findByStudentIdAndLessonId(studentInfo.getId(), lessonEntity.getId());
                    attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
                });
                progressResponse.setScoreList(scoreList);
            } else {
                progressResponse.setLessonCount(0);
                progressResponse.setScoreList(Collections.emptyList());
            }
            progressResponseList.add(progressResponse);
        });
        return progressResponseList;

    }
    @Transactional
    public Integer sendOTP(String phoneNumber) {
        userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new DataNotFoundException("User not found!"));
        verificationRepository.deleteByPhoneNumber(phoneNumber);
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPhoneNumber(phoneNumber);
        verificationCode.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        Random random = new Random();
        Integer code = 10000 + random.nextInt(90000);
        verificationCode.setCode(code);
        verificationRepository.save(verificationCode);
        return code;
    }


    public JwtResponse checkOTP(Integer code) {
        VerificationCode verificationCode = verificationRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Verification code not found!"));
        if(verificationCode.getExpirationDate().isAfter(LocalDateTime.now())){
            UserEntity user = userRepository.findByPhoneNumber(verificationCode.getPhoneNumber())
                    .orElseThrow(() -> new DataNotFoundException("User not found!"));
            List<String> tokens = jwtUtil.generateToken(user);
            return new JwtResponse(tokens.get(0),tokens.get(1),user.getRole());
        }throw  new RuntimeException("Verification code expired!");
    }


public Map<String, Object> getStatistics(UUID moduleId, String searchTerm, int pageNumber, int pageSize) {
    Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
    Page<StudentInfo> studentInfoPage;
    moduleRepository.findById(moduleId)
            .orElseThrow(() -> new DataNotFoundException("Module not found!"));
    SubjectEntity subjectEntity = subjectRepository.findByModules_Id(moduleId)
            .orElseThrow(() -> new DataNotFoundException("Subject not found!"));
    if(searchTerm != null) studentInfoPage = studentInfoRepository.findBySubject_IdWithSearchTerm(subjectEntity.getId(), searchTerm, pageable);
    else studentInfoPage = studentInfoRepository.findBySubject_Id(subjectEntity.getId(),pageable);

    List<StudentStatisticsDTO> studentStatisticsDTOList = getStudentStatisticsDTOS(studentInfoPage.getContent(),moduleId);

    Map<String, Object> responseMap = new LinkedHashMap<>();
    responseMap.put("pageNumber", studentInfoPage.getNumber() + 1);
    responseMap.put("totalPages", studentInfoPage.getTotalPages());
    responseMap.put("totalCount", studentInfoPage.getTotalElements());
    responseMap.put("pageSize", studentInfoPage.getSize());
    responseMap.put("hasPreviousPage", studentInfoPage.hasPrevious());
    responseMap.put("hasNextPage", studentInfoPage.hasNext());
    responseMap.put("data", studentStatisticsDTOList);

    return responseMap;
}
    private List<StudentStatisticsDTO> getStudentStatisticsDTOS(List<StudentInfo> studentInfoList, UUID moduleId) {
        List<StudentStatisticsDTO> studentStatisticsDTOList = new ArrayList<>();

        studentInfoList.forEach(studentInfo -> {
            StudentStatisticsDTO studentStatisticsDTO = new StudentStatisticsDTO();
            studentStatisticsDTO.setStudentName(studentInfo.getStudent().getName());
            studentStatisticsDTO.setStudentSurname(studentInfo.getStudent().getSurname());
            studentStatisticsDTO.setAvatar(studentInfo.getStudent().getAvatar());


            Optional<List<LessonEntity>> lessons = lessonRepository.findAllByModule_IdOrderByNumberAsc(moduleId);
            List<Integer> scoreList = new ArrayList<>();

            if (lessons.isPresent()) {
                lessons.get().forEach(lesson -> {
                    Optional<AttendanceEntity> attendance = attendanceRepository.findByStudent_IdAndLesson_IdAndStatusIs(studentInfo.getId(), lesson.getId(), CHECKED);
                    attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
                });
                studentStatisticsDTO.setLessonCount(lessons.get().size());
            } else {
                studentStatisticsDTO.setLessonCount(0);
            }

            studentStatisticsDTO.setScoreList(scoreList);
            studentStatisticsDTOList.add(studentStatisticsDTO);
        });

        return studentStatisticsDTOList;
    }


    public Map<String, Object> getStatistics2(UUID moduleId, String searchTerm, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<StudentInfo> studentInfoPage;
        moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found!"));
        SubjectEntity subjectEntity = subjectRepository.findByModules_Id(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found!"));
        if(searchTerm != null) studentInfoPage = studentInfoRepository.findBySubject_IdWithSearchTerm(subjectEntity.getId(), searchTerm, pageable);
        else studentInfoPage = studentInfoRepository.findBySubject_Id(subjectEntity.getId(),pageable);

        List<StudentStatisticsDTO2> studentStatisticsDTOList = new ArrayList<>();

        studentInfoPage.getContent().forEach(studentInfo ->{
            StudentStatisticsDTO2 studentStatisticsDTO = new StudentStatisticsDTO2();
            studentStatisticsDTO.setStudentName(studentInfo.getStudent().getName());
            studentStatisticsDTO.setStudentSurname(studentInfo.getStudent().getSurname());
            studentStatisticsDTO.setAvatar(studentInfo.getStudent().getAvatar());
            studentStatisticsDTO.setAttendanceDTOList(attendanceRepository.findAttendanceByStudentIdAndModuleId(studentInfo.getId(),moduleId));
            studentStatisticsDTOList.add(studentStatisticsDTO);
        });

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("pageNumber", studentInfoPage.getNumber() + 1);
        responseMap.put("totalPages", studentInfoPage.getTotalPages());
        responseMap.put("totalCount", studentInfoPage.getTotalElements());
        responseMap.put("pageSize", studentInfoPage.getSize());
        responseMap.put("hasPreviousPage", studentInfoPage.hasPrevious());
        responseMap.put("hasNextPage", studentInfoPage.hasNext());
        responseMap.put("lessonCount", lessonRepository.getLessonCount(moduleId));
        responseMap.put("data", studentStatisticsDTOList);

        return responseMap;
    }
}
