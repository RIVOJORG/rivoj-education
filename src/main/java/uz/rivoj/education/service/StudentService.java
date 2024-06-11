package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.StudentCreateRequest;
import uz.rivoj.education.dto.response.StudentResponse;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentInfoRepository studentInfoRepository;
    private final SubjectRepository subjectRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

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

    public String addStudent(StudentCreateRequest studentCreateRequest) {
        if (userRepository.findUserEntityByPhoneNumber(studentCreateRequest.getPhoneNumber()).isPresent()){
            throw new DataAlreadyExistsException("Student already exists with this phone number: " + studentCreateRequest.getPhoneNumber());}
        UserEntity userEntity = UserEntity.builder()
                .name(studentCreateRequest.getName())
                .password(studentCreateRequest.getPassword())
                .phoneNumber(studentCreateRequest.getPhoneNumber())
                .role(UserRole.STUDENT)
                .userStatus(UserStatus.UNBLOCK)
                .surname(studentCreateRequest.getSurname())
                .build();
        userRepository.save(userEntity);

        if (!subjectRepository.existsByTitle(studentCreateRequest.getSubject())){
            throw new DataNotFoundException("Subject not found with this title: " + studentCreateRequest.getSubject());}
        SubjectEntity subject = subjectRepository.findByTitle(studentCreateRequest.getSubject());

        if (moduleRepository.findFirstBySubjectOrderByNumberAsc(subject) == null){
            throw new DataNotFoundException("Module not found ");}
        ModuleEntity moduleEntity = moduleRepository.findFirstBySubjectOrderByNumberAsc(subject);

        if (lessonRepository.findFirstByModuleOrderByNumberAsc(moduleEntity) == null) {
            throw new DataNotFoundException("Lesson not found ");}
        LessonEntity lesson = lessonRepository.findFirstByModuleOrderByNumberAsc(moduleEntity);

        StudentInfo student = StudentInfo.builder()
                .birthday(studentCreateRequest.getBirthday())
                .coin(0)
                .student(userEntity)
                .subject(subject)
                .lesson(lesson)
                .currentModule(moduleEntity)
                .totalScore(0)
                .build();
        studentInfoRepository.save(student);
        return "Student successfully added";
    }
}
