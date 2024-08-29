package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.StudentCR;
import uz.rivoj.education.dto.request.StudentUpdate;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

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
            Optional<StudentInfo> studentInfo = studentInfoRepository.findStudentInfoByStudentId(userEntity.getId());
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
        if (userRepository.findUserEntityByPhoneNumber(studentCR.getPhoneNumber()).isPresent()){
            throw new DataAlreadyExistsException("Student already exists with this phone number: " + studentCR.getPhoneNumber());}
        UserEntity userEntity = UserEntity.builder()
                .name(studentCR.getName())
                .password(passwordEncoder.encode(studentCR.getPassword()))
                .phoneNumber(studentCR.getPhoneNumber())
                .role(UserRole.STUDENT)
                .userStatus(UserStatus.UNBLOCK)
                .surname(studentCR.getSurname())
                .build();
        if (!subjectRepository.existsByTitle(studentCR.getSubject())){
            throw new DataNotFoundException("Subject not found with this title: " + studentCR.getSubject());}
        SubjectEntity subject = subjectRepository.findByTitle(studentCR.getSubject());

        if (moduleRepository.findFirstBySubjectOrderByNumber(subject) == null){
            throw new DataNotFoundException("Module not found ");}
        ModuleEntity moduleEntity = moduleRepository.findFirstBySubjectOrderByNumber(subject);

        if (lessonRepository.findFirstByModuleOrderByNumberAsc(moduleEntity) == null) {
            throw new DataNotFoundException("Lesson not found ");}
        LessonEntity lesson = lessonRepository.findFirstByModuleOrderByNumberAsc(moduleEntity);

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

    public List<StudentStatisticsDTO> getStudentStatistics(String teacherId, Integer moduleNumber) {
        UserEntity teacher = userRepository.findById(UUID.fromString(teacherId))
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher(teacher);
        if (teacherInfo == null) {
            throw new RuntimeException("Teacher information not found");
        }
        ModuleEntity module = moduleRepository.findBySubjectAndNumber(teacherInfo.getSubject(), moduleNumber);

        if (module == null) {
            throw new RuntimeException("Module not found for the given module number");
        }

        List<LessonEntity> lessons = lessonRepository.findByModule(module);

        return studentInfoRepository.findByCurrentModule(module).stream()
                .map(studentInfo -> {
                    UserEntity student = studentInfo.getStudent();

                    StudentStatisticsDTO dto = new StudentStatisticsDTO();
                    dto.setStudentName(student.getName());
                    dto.setStudentSurname(student.getSurname());
                    dto.setAvatar(studentInfo.getAvatar());
                    dto.setScore(studentInfo.getTotalScore());
                    dto.setIsLessonOver(studentInfo.getIsLessonOver());

                    List<SpecialAttendanceResponse> attendanceResponses = new ArrayList<>();

                    for (LessonEntity lesson : lessons) {
                        attendanceRepository.findByStudentAndLessonEntity(studentInfo, lesson)
                                .ifPresent(attendance -> {
                                    SpecialAttendanceResponse response = new SpecialAttendanceResponse();
                                    response.setModuleNumber(moduleNumber);
                                    response.setLessonNumber(lesson.getNumber());
                                    response.setScore(attendance.getScore());
                                    attendanceResponses.add(response);
                                });
                    }

                    dto.setAttendanceList(attendanceResponses);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<AdminHomePageResponse> getStudentProgress(UUID subjectId) {
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found"));

        List<ModuleEntity> modules = moduleRepository.findAllBySubject(subject)
                .orElseThrow(() -> new DataNotFoundException("Modules not found"));

        List<TeacherInfo> teachers = teacherInfoRepository.findBySubject(subject);

        List<Integer> moduleCounts = new ArrayList<>();
        for (ModuleEntity module : modules) {
            moduleCounts.add(module.getNumber());
        }

        List<StudentStatisticsDTO> studentStatistics = new ArrayList<>();
        for (TeacherInfo teacher : teachers) {
            for (ModuleEntity module : modules) {
                studentStatistics.addAll(getStudentStatistics(teacher.getTeacher().getId().toString(), module.getNumber()));
            }
        }

        AdminHomePageResponse adminHomePageResponse = AdminHomePageResponse.builder()
                .modulesCounts(moduleCounts)
                .studentsCount(studentStatistics.size())
                .students(studentStatistics)
                .build();

        return Collections.singletonList(adminHomePageResponse);
    }


    public String changePassword() {
        List<UserEntity> all = userRepository.findAll();
        for (UserEntity user : all) {
            user.setPassword(passwordEncoder.encode("12345678"));
            userRepository.save(user);
        }
        return "Password changed";
    }

    @SneakyThrows
    public StudentResponse updateProfile(StudentUpdate studentUpdate, MultipartFile picture, UUID studentId) {
        StudentInfo studentInfo = studentInfoRepository.findStudentInfoByStudentId(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        UserEntity userEntity = userRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found!"));
        if(studentUpdate.getBirthday() != null){
            studentInfo.setBirthday(studentInfo.getBirthday());}
        if(studentUpdate.getSurname() != null){
            userEntity.setSurname(studentUpdate.getSurname());}
        if(studentUpdate.getPhoneNumber() != null){
            userEntity.setPhoneNumber(studentUpdate.getPhoneNumber());}
        if(studentUpdate.getName() != null){
            userEntity.setName(studentUpdate.getName());}
        if(studentUpdate.getPassword() != null){
            userEntity.setPassword(passwordEncoder.encode(studentUpdate.getPassword()));
        }
        if(!picture.isEmpty()){
            String filename = userEntity.getName() + "_ProfilePicture";
            String avatarPath = uploadService.uploadFile(picture, filename);
            userEntity.setAvatar(avatarPath);
        }
        userRepository.save(userEntity);
        studentInfoRepository.save(studentInfo);
        StudentResponse response = modelMapper.map(userEntity, StudentResponse.class);
        response.setBirth(studentInfo.getBirthday());
    return  response;
    }

    public List<StudentResponse> getAllMyStudent(int page, int size, UUID userId) {
        UserEntity teacher = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher(teacher);
        if (teacherInfo == null) {
            throw new RuntimeException("Teacher information not found");
        }

        List<SubjectEntity> subjects = subjectRepository.findAllByTeachersTeacherId(teacherInfo.getTeacher().getId());
        System.out.println("Fetched subjects: " + subjects);

        if (subjects.isEmpty()) {
            throw new RuntimeException("No subjects found for the teacher");
        }

        Pageable pageable = PageRequest.of(page, size);
        List<StudentInfo> students = studentInfoRepository.findAllBySubjectIn(subjects, pageable);
        System.out.println("Fetched students: " + students);

        return students.stream()
                .map(this::convertToStudentResponse)
                .collect(Collectors.toList());
    }


    private StudentResponse convertToStudentResponse(StudentInfo studentInfo) {
        return new StudentResponse(
                studentInfo.getStudent().getId().toString(),
                studentInfo.getStudent().getName(),
                studentInfo.getStudent().getSurname(),
                studentInfo.getAvatar(),
                studentInfo.getStudent().getPhoneNumber(),
                studentInfo.getBirthday(),
                studentInfo.getSubject() != null ? studentInfo.getSubject().getId() : null,
                studentInfo.getLesson() != null ? studentInfo.getLesson().getId() : null
        );
    }
}
