package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.TeacherInfoRequest;
import uz.rivoj.education.dto.response.StudentResponse;
import uz.rivoj.education.entity.SubjectEntity;
import uz.rivoj.education.entity.TeacherInfo;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.entity.UserRole;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.StudentInfoRepository;
import uz.rivoj.education.repository.SubjectRepository;
import uz.rivoj.education.repository.TeacherInfoRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherInfoRepository teacherInfoRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    public String createTeacher(TeacherInfoRequest teacherInfo) {
        UserEntity userEntity = userRepository.findById(teacherInfo.getUserId()).orElseThrow(
                () -> new DataNotFoundException("User not found"));

        if (!subjectRepository.existsByTitle(teacherInfo.getSubject())){
            throw new DataNotFoundException("Subject not found with this title: " + teacherInfo.getSubject());}
        SubjectEntity subject = subjectRepository.findByTitle(teacherInfo.getSubject());

        TeacherInfo teacher = TeacherInfo.builder()
                .about(teacherInfo.getAbout())
                .subject(subject)
                .teacher(userEntity)
                .build();
        modelMapper.map(teacherInfo, TeacherInfo.class);
        teacherInfoRepository.save(teacher);
        return "successfully added";
    }


}
