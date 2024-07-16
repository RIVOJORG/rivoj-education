package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.TeacherCR;
import uz.rivoj.education.entity.SubjectEntity;
import uz.rivoj.education.entity.TeacherInfo;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.entity.UserRole;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.SubjectRepository;
import uz.rivoj.education.repository.TeacherInfoRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherInfoRepository teacherInfoRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    public String createTeacher(TeacherCR teacherCr) {
        Optional<UserEntity> userByPhoneNumber = userRepository.findByPhoneNumber(teacherCr.getPhoneNumber());
        if(userByPhoneNumber.isPresent()){
            return ("Phone number already registered!");
        }
        if (!subjectRepository.existsByTitle(teacherCr.getSubject())){
            throw new DataNotFoundException("Subject not found with this title: " + teacherCr.getSubject());}
        SubjectEntity subject = subjectRepository.findByTitle(teacherCr.getSubject());
        UserEntity user = modelMapper.map(teacherCr, UserEntity.class);
        user.setRole(UserRole.TEACHER);
        user.setUserStatus(UserStatus.UNBLOCK);
        TeacherInfo teacher = TeacherInfo.builder()
                .about(teacherCr.getAbout())
                .subject(subject)
                .teacher(user)
                .build();
        userRepository.save(user);
        teacherInfoRepository.save(teacher);
        return "Created";
    }


}
