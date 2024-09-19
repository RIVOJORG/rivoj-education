package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

import java.io.IOException;
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

    public List<StudentResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<UserEntity> all = userRepository.findAllByRole(UserRole.STUDENT,pageable).getContent();
        List<StudentResponse> responses = new ArrayList<>();
        for (UserEntity userEntity : all) {
            StudentResponse studentResponse = modelMapper.map(userEntity, StudentResponse.class);
            Optional<StudentInfo> studentInfo = studentInfoRepository.findByStudentId(userEntity.getId());
            if (studentInfo.isPresent()) {
                studentResponse.setId(String.valueOf(studentInfo.get().getId()));
                studentResponse.setAvatar(studentInfo.get().getAvatar());
                studentResponse.setBirth(studentInfo.get().getBirthday());
                responses.add(studentResponse);
                responses.add(studentResponse); }

            }
        return responses;
    }
    public String addStudent(StudentCR studentCR) {
        if (userRepository.findByPhoneNumber(studentCR.getPhoneNumber()).isPresent()){
            throw new DataAlreadyExistsException("Student already exists with this phone number: " + studentCR.getPhoneNumber());}
        Optional<SubjectEntity> subjectOptional = subjectRepository.findByTitle(studentCR.getSubject());
        if(subjectOptional.isEmpty()){
            throw new DataNotFoundException("Subject not found with this title: " + studentCR.getSubject());
        }

        UserEntity userEntity = UserEntity.builder()
                .name(studentCR.getName())
                .password(passwordEncoder.encode(studentCR.getPassword()))
                .phoneNumber(studentCR.getPhoneNumber())
                .role(UserRole.STUDENT)
                .userStatus(UserStatus.UNBLOCK)
                .surname(studentCR.getSurname())
                .build();
        SubjectEntity subject = subjectOptional.get();
        ModuleEntity moduleEntity = moduleRepository.findFirstBySubject_IdOrderByNumber(subject.getId());
        Optional<LessonEntity> firstLessonOptional = lessonRepository.findFirstByModule_IdOrderByNumberAsc(moduleEntity.getId());
        if(firstLessonOptional.isEmpty()){
            throw new DataNotFoundException("Lesson not found  in this course: " + studentCR.getSubject());
        }
        StudentInfo student = StudentInfo.builder()
                .birthday(studentCR.getBirthday())
                .coin(0)
                .student(userEntity)
                .subject(subject)
                .lesson(firstLessonOptional.get())
                .currentModule(moduleEntity)
                .totalScore(0)
                .build();
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
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Pageable pageable = PageRequest.of(page, size);
        List<StudentInfo> students = studentInfoRepository.findBySubject_Id(teacherInfo.getSubject().getId(), pageable)
                .orElseThrow(() -> new RuntimeException("Students not found"));
        return students.stream()
                .map(entity -> modelMapper.map(entity, StudentResponse.class))
                .collect(Collectors.toList());
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
        Optional<AttendanceEntity> attendance = attendanceRepository.findByStudentIdAndLessonId(userId,attendanceCR.getLessonId());
        AttendanceEntity attendanceEntity ;
        if(attendance.isPresent()) {
             attendanceEntity = attendance.get();
                    attendanceEntity.setId(attendanceEntity.getId());
                    attendanceEntity.setAnswers(homeworkPaths);
                    attendanceEntity.setCoin(null);
                    attendanceEntity.setLesson(lesson);
                    attendanceEntity.setStudent(student);
                    attendanceEntity.setAttemptsNumber(attendanceEntity.getAttemptsNumber() + 1);
                    attendanceEntity.setStatus(UNCHECKED);
        }else {
            attendanceEntity = AttendanceEntity.builder()
                    .answers(homeworkPaths)
                    .coin(0)
                    .lesson(lesson)
                    .student(student)
                    .status(UNCHECKED)
                    .attemptsNumber(1)
                    .build();
        }
        attendanceRepository.save(attendanceEntity);
        return "Uploaded!";
    }

    public List<StudentStatisticsDTO> getStudentStatisticsByModuleAndStatus(String name, UUID moduleId) {
        List<StudentInfo> studentInfoList = studentInfoRepository.findByCurrentModule_Id(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        List<LessonEntity> lessonEntityList = lessonRepository.findAllByModule_IdOrderByNumberAsc(moduleId)
                .orElseThrow(() -> new RuntimeException("Lessons not found!"));
        return getStudentStatisticsByModule(studentInfoList,lessonEntityList);
    }


    public List<StudentStatisticsDTO> getAllStudentStatisticsOnCurrentModule(UUID teacherId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found!"));
        List<StudentInfo> studentInfoList = studentInfoRepository.findBySubjectId(teacherInfo.getSubject().getId())
                .orElseThrow(() -> new DataNotFoundException("Student does not exist yet!"));
        List<StudentStatisticsDTO> studentStatisticsDTOList = new ArrayList<>();
        studentInfoList.forEach(studentInfo -> {
            List<LessonEntity> lessons = lessonRepository.findAllByModule_IdOrderByNumberAsc(studentInfo.getCurrentModule().getId())
                    .orElseThrow(() -> new RuntimeException("Lessons not found!"));
            StudentStatisticsDTO studentStatistics = new StudentStatisticsDTO();
            studentStatistics.setStudentName(studentInfo.getStudent().getName());
            studentStatistics.setStudentSurname(studentInfo.getStudent().getSurname());
            studentStatistics.setAvatar(studentInfo.getAvatar());
            List<Integer> scoreList = new ArrayList<>();
            lessons.forEach(lessonEntity -> {
                Optional<AttendanceEntity> attendance = attendanceRepository.findByStudent_IdAndLesson_IdAndStatusIs(studentInfo.getId(), lessonEntity.getId(),CHECKED);
                attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
            });
            studentStatistics.setScoreList(scoreList);
            studentStatisticsDTOList.add(studentStatistics);
        });
        return studentStatisticsDTOList;
    }

    public List<StudentStatisticsDTO> getStudentStatisticsByModule(List<StudentInfo> students, List<LessonEntity> lessons){
        List<StudentStatisticsDTO> studentStatisticsDTOList = new ArrayList<>();
        students.forEach(studentInfo -> {
            StudentStatisticsDTO studentStatistics = new StudentStatisticsDTO();
            studentStatistics.setStudentName(studentInfo.getStudent().getName());
            studentStatistics.setStudentSurname(studentInfo.getStudent().getSurname());
            studentStatistics.setAvatar(studentInfo.getAvatar());
            List<Integer> scoreList = new ArrayList<>();
            lessons.forEach(lessonEntity -> {
                Optional<AttendanceEntity> attendance = attendanceRepository.findByStudent_IdAndLesson_IdAndStatusIs(studentInfo.getId(), lessonEntity.getId(),CHECKED);
                attendance.ifPresent(attendanceEntity -> scoreList.add(attendanceEntity.getScore()));
                });
            studentStatistics.setScoreList(scoreList);
            studentStatisticsDTOList.add(studentStatistics);
        });
        return studentStatisticsDTOList;
    }

    public ProgressResponse getStudentProgress(UUID moduleId,UUID userId) {
        ModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found!"));
        SubjectEntity subject = subjectRepository.findById(module.getSubject().getId()).orElseThrow(
                () -> new RuntimeException("Subject not found!")
        );
        List<LessonEntity> lessonEntityList = lessonRepository.findAllByModule_IdOrderByNumberAsc(moduleId)
                .orElseThrow(() -> new RuntimeException("Lesson not found!"));
        ProgressResponse newProgressResponse = new ProgressResponse();
        newProgressResponse.setModuleCount(subject.getModules().size()+1);
        List<Integer> scoreList = new ArrayList<>();
        lessonEntityList.forEach(lessonEntity -> {
            Optional<AttendanceEntity> attendance = attendanceRepository.findByLesson_Id(lessonEntity.getId());
            if (attendance.isPresent() && attendance.get().getStatus().equals(CHECKED)) {
                scoreList.add(attendance.get().getScore());
            }
        });
        newProgressResponse.setScoreList(scoreList);
        return newProgressResponse;
    }
}
