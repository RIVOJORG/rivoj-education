package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
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

    public List<StudentResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<UserEntity> all = userRepository.findAllByRole(UserRole.STUDENT,pageable).getContent();
        List<StudentResponse> responses = new ArrayList<>();
        for (UserEntity userEntity : all) {
            StudentResponse studentResponse = modelMapper.map(userEntity, StudentResponse.class);
            Optional<StudentInfo> studentInfo = studentInfoRepository.findByStudentId(userEntity.getId());
            if (studentInfo.isPresent()) {
                studentResponse.setId((studentInfo.get().getId()));
                studentResponse.setAvatar(studentInfo.get().getAvatar());
                studentResponse.setBirth(studentInfo.get().getBirthday());
                studentResponse.setSubjectId(studentInfo.get().getSubject().getId());
                studentResponse.setCurrentLessonId(studentInfo.get().getLesson().getId());
                responses.add(studentResponse);
            }

            }
        return responses;
    }
    public String addStudent(StudentCR studentCR) {
        // Check if a student already exists with the provided phone number
        if (userRepository.findByPhoneNumber(studentCR.getPhoneNumber()).isPresent()) {
            throw new DataAlreadyExistsException("Student already exists with this phone number: " + studentCR.getPhoneNumber());
        }

        // Find the subject based on the subject title from the DTO
        SubjectEntity subject = subjectRepository.findByTitle(studentCR.getSubject())
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this title: " + studentCR.getSubject()));

        // Find the module based on the provided starterModule number in the DTO
        ModuleEntity moduleEntity = moduleRepository.findBySubject_IdAndNumber(subject.getId(), studentCR.getStarterModule())
                .orElseThrow(() -> new DataNotFoundException("Module not found with subject ID: " + subject.getId()
                        + " and module number: " + studentCR.getStarterModule()));

        // Find the first lesson of the module
        LessonEntity lesson = lessonRepository.findFirstByModule_IdOrderByNumberAsc(moduleEntity.getId())
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with module ID: " + moduleEntity.getId()));

        // Create a new UserEntity for the student
        UserEntity userEntity = UserEntity.builder()
                .name(studentCR.getName())
                .password(passwordEncoder.encode(studentCR.getPassword()))
                .phoneNumber(studentCR.getPhoneNumber())
                .role(UserRole.STUDENT)
                .userStatus(UserStatus.UNBLOCK)
                .surname(studentCR.getSurname())
                .build();

        // Create a new StudentInfo entity and set the currentModule based on the starterModule
        StudentInfo student = StudentInfo.builder()
                .birthday(studentCR.getBirthday())
                .coin(0)
                .student(userEntity)
                .subject(subject)
                .lesson(lesson)
                .currentModule(moduleEntity)  // Set the module as the student's current module
                .totalScore(0)
                .build();

        // Save the user and student information in the repository
        userRepository.save(userEntity);
        studentInfoRepository.save(student);

        return "Created";
    }


    public String changePassword() {
        List<UserEntity> all = userRepository.findAll();
        for (UserEntity user : all) {
            user.setPassword(passwordEncoder.encode("12345678"));
            userRepository.save(user);
        }
        return "Password changed";
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
                studentInfo.getTotalScore()

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

    public List<StudentStatisticsDTO> getStudentStatisticsByModule(UUID moduleId) {
        List<StudentStatisticsDTO> studentStatisticsDTOs = new ArrayList<>();
        ModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found!"));
        SubjectEntity subjectEntity = subjectRepository.findByModules_Id(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found!"));
        List<StudentInfo> studentInfoList = studentInfoRepository.findBySubject_Id(subjectEntity.getId())
                .orElseThrow(() -> new DataNotFoundException("Students not found"));
        studentInfoList.forEach(studentInfo -> {
            StudentStatisticsDTO studentStatisticsDTO = new StudentStatisticsDTO();
            studentStatisticsDTO.setStudentName(studentInfo.getStudent().getName());
            studentStatisticsDTO.setStudentSurname(studentInfo.getStudent().getSurname());
            studentStatisticsDTO.setAvatar(studentInfo.getAvatar());
            List<Integer> scoreList = new ArrayList<>();
            Optional<List<LessonEntity>> lessonEntities = lessonRepository.findAllByModule_IdOrderByNumberAsc(studentInfo.getCurrentModule().getId());
            if (lessonEntities.isPresent()) {
                List<LessonEntity> lessonEntityList = lessonEntities.get();
                lessonEntityList.forEach(lesson -> {
                    Optional<AttendanceEntity> attendance = attendanceRepository.findByStudent_IdAndLesson_IdAndStatusIs(studentInfo.getId(), lesson.getId(),CHECKED);
                    attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
                });
                studentStatisticsDTO.setLessonCount(lessonEntityList.size()+1);
                studentStatisticsDTO.setScoreList(scoreList);
                studentStatisticsDTOs.add(studentStatisticsDTO);
            }
        });
        return studentStatisticsDTOs;
    }

    public List<StudentStatisticsDTO> getAllStudentStatisticsOnCurrentModule(UUID teacherId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
        List<StudentInfo> studentInfoList = studentInfoRepository.findBySubjectId(teacherInfo.getSubject().getId())
                .orElseThrow(() -> new DataNotFoundException("Students not found!"));
        List<StudentStatisticsDTO> studentStatisticsDTOList = new ArrayList<>();
        studentInfoList.forEach(studentInfo -> {
            StudentStatisticsDTO studentStatisticsDTO = new StudentStatisticsDTO();
            studentStatisticsDTO.setStudentName(studentInfo.getStudent().getName());
            studentStatisticsDTO.setStudentSurname(studentInfo.getStudent().getSurname());
            studentStatisticsDTO.setAvatar(studentInfo.getAvatar());
            List<Integer> scoreList = new ArrayList<>();
            Optional<List<LessonEntity>> lessonEntities = lessonRepository.findAllByModule_IdOrderByNumberAsc(studentInfo.getCurrentModule().getId());
            if (lessonEntities.isPresent()) {
                List<LessonEntity> lessonEntityList = lessonEntities.get();
                lessonEntityList.forEach(lesson -> {
                    Optional<AttendanceEntity> attendance = attendanceRepository.findByStudent_IdAndLesson_IdAndStatusIs(studentInfo.getId(), lesson.getId(),CHECKED);
                    attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
                });
                studentStatisticsDTO.setScoreList(scoreList);
                studentStatisticsDTOList.add(studentStatisticsDTO);
            }

        });
        return studentStatisticsDTOList;
    }


    public ProgressResponse getStudentProgress(UUID moduleId, UUID studentId) {
        StudentInfo studentInfo = studentInfoRepository.findByStudentId(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        Optional<List<LessonEntity>> lessonEntities = lessonRepository.findAllByModule_IdOrderByNumberAsc(moduleId);
        ProgressResponse progressResponse = new ProgressResponse();
        if (lessonEntities.isPresent()) {
            List<LessonEntity> lessons = lessonEntities.get();
            progressResponse.setLessonCount(lessons.size()+1);
            List<Integer> scoreList = new ArrayList<>();
            lessons.forEach(lessonEntity -> {
                Optional<AttendanceEntity> attendance = attendanceRepository.findByStudentIdAndLessonId(studentInfo.getId(), lessonEntity.getId());
                attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
            });
            progressResponse.setScoreList(scoreList);
        }
        return progressResponse;

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

    public List<StudentStatisticsDTO> getAllStudentStatisticsOnCurrentModule2(UUID subjectId) {
        List<StudentInfo> studentInfoList = studentInfoRepository.findBySubjectId(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Students not found!"));
        List<StudentStatisticsDTO> studentStatisticsDTOList = new ArrayList<>();
        studentInfoList.forEach(studentInfo -> {
            StudentStatisticsDTO studentStatisticsDTO = new StudentStatisticsDTO();
            studentStatisticsDTO.setStudentName(studentInfo.getStudent().getName());
            studentStatisticsDTO.setStudentSurname(studentInfo.getStudent().getSurname());
            studentStatisticsDTO.setAvatar(studentInfo.getAvatar());
            List<Integer> scoreList = new ArrayList<>();
            Optional<List<LessonEntity>> lessonEntities = lessonRepository.findAllByModule_IdOrderByNumberAsc(studentInfo.getCurrentModule().getId());
            if (lessonEntities.isPresent()) {
                List<LessonEntity> lessons = lessonEntities.get();
                lessons.forEach(lesson -> {
                    Optional<AttendanceEntity> attendance = attendanceRepository.findByStudent_IdAndLesson_IdAndStatusIs(studentInfo.getId(), lesson.getId(),CHECKED);
                    attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
                });
                studentStatisticsDTO.setLessonCount(lessons.size()+1);
                studentStatisticsDTO.setScoreList(scoreList);
                studentStatisticsDTOList.add(studentStatisticsDTO);
            }
        });
        return studentStatisticsDTOList;

    }
}
