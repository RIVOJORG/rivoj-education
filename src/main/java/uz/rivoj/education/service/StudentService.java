package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.StudentCR;
import uz.rivoj.education.dto.response.SpecialAttendanceResponse;
import uz.rivoj.education.dto.response.StudentResponse;
import uz.rivoj.education.dto.response.StudentStatisticsDTO;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    public List<StudentResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<UserEntity> all = userRepository.findAllByRole(UserRole.STUDENT,pageable).getContent();
        List<StudentResponse> responses = new ArrayList<>();
        for (UserEntity userEntity : all) {
            StudentResponse studentResponse = modelMapper.map(userEntity, StudentResponse.class);
             studentInfoRepository.findStudentInfoByStudentId(userEntity.getId()).get();
             responses.add(studentResponse);
        }
        return responses;
    }

    public String addStudent(StudentCR studentCR) {
        if (userRepository.findUserEntityByPhoneNumber(studentCR.getPhoneNumber()).isPresent()){
            throw new DataAlreadyExistsException("Student already exists with this phone number: " + studentCR.getPhoneNumber());}
        UserEntity userEntity = UserEntity.builder()
                .name(studentCR.getName())
                .password(studentCR.getPassword())
                .phoneNumber(studentCR.getPhoneNumber())
                .role(UserRole.STUDENT)
                .userStatus(UserStatus.UNBLOCK)
                .surname(studentCR.getSurname())
                .build();
        if (!subjectRepository.existsByTitle(studentCR.getSubject())){
            throw new DataNotFoundException("Subject not found with this title: " + studentCR.getSubject());}
        SubjectEntity subject = subjectRepository.findByTitle(studentCR.getSubject());

        if (moduleRepository.findFirstBySubjectOrderByNumberAsc(subject) == null){
            throw new DataNotFoundException("Module not found ");}
        ModuleEntity moduleEntity = moduleRepository.findFirstBySubjectOrderByNumberAsc(subject);

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

    public List<StudentStatisticsDTO> getStudentStatistics(String teacher, Integer moduleNumber) {
        Optional<TeacherInfo> teacherInfo = teacherInfoRepository.findById(UUID.fromString(teacher));
        if (teacherInfo.isEmpty()) {
            throw new RuntimeException("Teacher not found");
        }

        SubjectEntity subject = teacherInfo.get().getSubject();
        ModuleEntity module = moduleRepository.findBySubjectAndNumber(subject, moduleNumber);
        if (module == null) {
            throw new RuntimeException("Module not found");
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

                    // Create a list to hold attendance responses for each lesson
                    List<SpecialAttendanceResponse> attendanceResponses = new ArrayList<>();

                    for (LessonEntity lesson : lessons) {
                        AttendanceEntity attendance = attendanceRepository.findByStudentAndLessonEntity(studentInfo, lesson);
                        if (attendance != null) {
                            SpecialAttendanceResponse response = new SpecialAttendanceResponse();
                            response.setModuleNumber(moduleNumber);
                            response.setLessonNumber(lesson.getNumber());  // Assuming lessonNumber is a field in LessonEntity
                            response.setScore(attendance.getScore());
                            attendanceResponses.add(response);
                        }
                    }

                    dto.setAttendanceList(attendanceResponses);
                    return dto;
                })
                .collect(Collectors.toList());
    }



}
