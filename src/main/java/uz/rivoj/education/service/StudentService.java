package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.AttendanceCR;
import uz.rivoj.education.dto.request.StudentCR;
import uz.rivoj.education.dto.request.StudentUpdate;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;
import org.springframework.data.domain.Pageable;
import uz.rivoj.education.service.jwt.JwtUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CommentRepository commentRepository;
    private final ModuleService moduleService;


    public String addStudent(StudentCR studentCR) {
        if (userRepository.findByPhoneNumber(studentCR.getPhoneNumber()).isPresent()) {
            throw new DataAlreadyExistsException("Student already exists with this phone number: " + studentCR.getPhoneNumber());
        }

        SubjectEntity subject = subjectRepository.findByTitle(studentCR.getSubject())
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this title: " + studentCR.getSubject()));

        ModuleEntity moduleEntity = moduleRepository.findBySubject_IdAndNumber(subject.getId(), studentCR.getStarterModule())
                .orElseThrow(() -> new DataNotFoundException("Module not found with subject ID: " + subject.getId()
                        + " and module number: " + studentCR.getStarterModule()));

        LessonEntity lesson = lessonRepository.findFirstByModule_IdOrderByNumberAsc(moduleEntity.getId())
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with module ID: " + moduleEntity.getId()));

        UserEntity userEntity = UserEntity.builder()
                .name(studentCR.getName())
                .password(passwordEncoder.encode(studentCR.getPassword()))
                .phoneNumber(studentCR.getPhoneNumber())
                .role(UserRole.STUDENT)
                .userStatus(UserStatus.UNBLOCK)
                .surname(studentCR.getSurname())
                .build();

        StudentInfo student = StudentInfo.builder()
                .birthday(studentCR.getBirthday())
                .coin(0)
                .student(userEntity)
                .subject(subject)
                .lesson(lesson)
                .currentModule(moduleEntity)
                .totalScore(0)
                .build();

        userRepository.save(userEntity);
        studentInfoRepository.save(student);

        return "Created";
    }



    public StudentResponse updateProfile(StudentUpdate studentUpdate, UUID studentId) {
        StudentInfo studentInfo = studentInfoRepository.findByStudentId(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        UserEntity userEntity = userRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        if (studentUpdate.getBirthday() != null) {
            studentInfo.setBirthday(studentUpdate.getBirthday());
        }
        if (studentUpdate.getSurname() != null) {
            userEntity.setSurname(studentUpdate.getSurname());
        }
        if (studentUpdate.getPhoneNumber() != null) {
            System.out.println("studentUpdate.getPhoneNumber() = " + studentUpdate.getPhoneNumber());
            System.out.println("studentUpdate.getPhoneNumber().isEmpty() = " + studentUpdate.getPhoneNumber().isEmpty());
            userEntity.setPhoneNumber(studentUpdate.getPhoneNumber());
        }
        if (studentUpdate.getName() != null) {
            userEntity.setName(studentUpdate.getName());
        }
        if (studentUpdate.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(studentUpdate.getPassword()));
        }
        userRepository.save(userEntity);
        studentInfoRepository.save(studentInfo);
        StudentResponse response = modelMapper.map(userEntity, StudentResponse.class);
        response.setBirth(studentInfo.getBirthday());
        response.setSubjectId(studentInfo.getSubject().getId());
        response.setCurrentLessonId(studentInfo.getLesson().getId());
        return response;
    }


    @SneakyThrows
    public String updateProfilePicture(MultipartFile picture, UUID userId)  {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        String filename = userEntity.getName() + "_ProfilePicture";
        String avatarPath = uploadService.uploadFile(picture, filename);
        userEntity.setAvatar(avatarPath);
        userRepository.save(userEntity);
        return "Profile picture changed";
    }

    public List<StudentResponse> getAllMyStudent(int page, int size, UUID teacherId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
        Pageable pageable = PageRequest.of(page, size);
        List<StudentInfo> students = studentInfoRepository.findBySubject_Id(teacherInfo.getSubject().getId(), pageable)
                .orElseThrow(() -> new DataNotFoundException("Students not found!"));
        System.out.println("Fetched students: " + students);
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
                studentInfo.getAvatar(),
                studentInfo.getStudent().getPhoneNumber(),
                studentInfo.getBirthday(),
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

    public String uploadHomework(AttendanceCR attendanceCR, UUID userId,List<MultipartFile> files) {
        StudentInfo student = studentInfoRepository.findByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        LessonEntity lesson = lessonRepository.findById(attendanceCR.getLessonId())
                .orElseThrow(() -> new DataNotFoundException("Lesson not found!"));
        List<String> homeworkPaths = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String fileName = user.getName() + "'s_homework";
            try {
                homeworkPaths.add(uploadService.uploadFile(multipartFile, fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        AttendanceEntity attendanceEntity = AttendanceEntity.builder()
                .answers(homeworkPaths)
                .coin(0)
                .lesson(lesson)
                .student(student)
                .status(UNCHECKED)
                .build();
        attendanceRepository.save(attendanceEntity);
        return "Uploaded!";
    }



//    public List<StudentStatisticsDTO> getAllStudentStatisticsOnCurrentModule(UUID teacherId) {
//        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
//                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
//        List<StudentInfo> studentInfoList = studentInfoRepository.findBySubjectId(teacherInfo.getSubject().getId())
//                .orElseThrow(() -> new DataNotFoundException("Students not found!"));
//        List<StudentStatisticsDTO> studentStatisticsDTOList = new ArrayList<>();
//        studentInfoList.forEach(studentInfo -> {
//            StudentStatisticsDTO studentStatisticsDTO = new StudentStatisticsDTO();
//            studentStatisticsDTO.setStudentName(studentInfo.getStudent().getName());
//            studentStatisticsDTO.setStudentSurname(studentInfo.getStudent().getSurname());
//            studentStatisticsDTO.setAvatar(studentInfo.getAvatar());
//            List<Integer> scoreList = new ArrayList<>();
//            Optional<List<LessonEntity>> lessonEntities = lessonRepository.findAllByModule_IdOrderByNumberAsc(studentInfo.getCurrentModule().getId());
//            if (lessonEntities.isPresent()) {
//                List<LessonEntity> lessonEntityList = lessonEntities.get();
//                lessonEntityList.forEach(lesson -> {
//                    Optional<AttendanceEntity> attendance = attendanceRepository.findByStudent_IdAndLesson_IdAndStatusIs(studentInfo.getId(), lesson.getId(),CHECKED);
//                    attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
//                });
//                studentStatisticsDTO.setScoreList(scoreList);
//                studentStatisticsDTOList.add(studentStatisticsDTO);
//            }
//
//        });
//        return studentStatisticsDTOList;
//    }




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
                progressResponse.setLessonCount(lessons.size() + 1);
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
            return new JwtResponse(jwtUtil.generateToken(user),user.getRole());
        }throw  new RuntimeException("Verification code expired!");
    }

//    public List<StudentStatisticsDTO> getAllStudentStatisticsOnCurrentModule(UUID subjectId) {
//        List<StudentInfo> studentInfoList = studentInfoRepository.findBySubjectId(subjectId)
//                .orElseThrow(() -> new DataNotFoundException("Students not found!"));
//        List<StudentStatisticsDTO> studentStatisticsDTOList = new ArrayList<>();
//        return getStudentStatisticsDTOS(studentInfoList, studentStatisticsDTOList);
//
//    }
//    public List<StudentStatisticsDTO> getStudentStatisticsByModule(UUID moduleId) {
//        List<StudentStatisticsDTO> studentStatisticsDTOs = new ArrayList<>();
//        moduleRepository.findById(moduleId)
//                .orElseThrow(() -> new DataNotFoundException("Module not found!"));
//        SubjectEntity subjectEntity = subjectRepository.findByModules_Id(moduleId)
//                .orElseThrow(() -> new DataNotFoundException("Subject not found!"));
//        List<StudentInfo> studentInfoList = studentInfoRepository.findBySubject_Id(subjectEntity.getId())
//                .orElseThrow(() -> new DataNotFoundException("Students not found"));
//        return getStudentStatisticsDTOS(studentInfoList, studentStatisticsDTOs);
//    }
public Map<String, Object> getStatistics(UUID moduleId, UUID subjectId, String searchTerm, int pageNumber, int pageSize) {
    Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
    Page<StudentInfo> studentInfoPage;

    if (moduleId == null) {
        studentInfoPage = studentInfoRepository.findBySubjectIdWithSearchTerm(subjectId, searchTerm, pageable);
    } else {
        moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found!"));
        SubjectEntity subjectEntity = subjectRepository.findByModules_Id(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found!"));
        studentInfoPage = studentInfoRepository.findBySubject_IdWithSearchTerm(subjectEntity.getId(), searchTerm, pageable);
    }

    List<StudentStatisticsDTO> studentStatisticsDTOList = getStudentStatisticsDTOS(studentInfoPage.getContent());

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
    private List<StudentStatisticsDTO> getStudentStatisticsDTOS(List<StudentInfo> studentInfoList) {
        List<StudentStatisticsDTO> studentStatisticsDTOList = new ArrayList<>();
        studentInfoList.forEach(studentInfo -> {
            StudentStatisticsDTO studentStatisticsDTO = new StudentStatisticsDTO();
            studentStatisticsDTO.setStudentName(studentInfo.getStudent().getName());
            studentStatisticsDTO.setStudentSurname(studentInfo.getStudent().getSurname());
            studentStatisticsDTO.setAvatar(studentInfo.getAvatar());
            List<Integer> scoreList = new ArrayList<>();
            Optional<List<LessonEntity>> lessons = lessonRepository.findAllByModule_IdOrderByNumberAsc(studentInfo.getCurrentModule().getId());
            if (lessons.isPresent()) {
                lessons.get().forEach(lesson -> {
                    Optional<AttendanceEntity> attendance = attendanceRepository.findByStudent_IdAndLesson_IdAndStatusIs(studentInfo.getId(), lesson.getId(), CHECKED);
                    attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
                });
                studentStatisticsDTO.setLessonCount(lessons.get().size() + 1);
            }
            studentStatisticsDTO.setScoreList(scoreList);
            studentStatisticsDTOList.add(studentStatisticsDTO);
        });
        return studentStatisticsDTOList;
    }



}
