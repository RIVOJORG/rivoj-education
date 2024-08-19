package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.TeacherCR;
import uz.rivoj.education.dto.request.TeacherUpdate;
import uz.rivoj.education.dto.response.StudentResponse;
import uz.rivoj.education.dto.response.TeacherResponse;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.SubjectRepository;
import uz.rivoj.education.repository.TeacherInfoRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherInfoRepository teacherInfoRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;

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
        user.setPassword(passwordEncoder.encode(teacherCr.getPassword()));
        TeacherInfo teacher = TeacherInfo.builder()
                .about(teacherCr.getAbout())
                .subject(subject)
                .teacher(user)
                .build();
        userRepository.save(user);
        teacherInfoRepository.save(teacher);
        return "Created";
    }


    @SneakyThrows
    public TeacherResponse updateProfile(TeacherUpdate teacherUpdate, MultipartFile picture, UUID teacherId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findTeacherInfoByTeacherId(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
        UserEntity userEntity = userRepository.findById(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
        if(teacherUpdate.getBirthday() != null){
            teacherInfo.setBirthday(teacherInfo.getBirthday());}
        if(teacherUpdate.getSurname() != null){
            userEntity.setSurname(teacherUpdate.getSurname());}
        if(teacherUpdate.getPhoneNumber() != null){
            userEntity.setPhoneNumber(teacherUpdate.getPhoneNumber());}
        if(teacherUpdate.getName() != null){
            userEntity.setName(teacherUpdate.getName());}
        if(teacherUpdate.getPassword() != null){
            userEntity.setPassword(passwordEncoder.encode(teacherUpdate.getPassword()));
        }
        if(!picture.isEmpty()){
            String filename = userEntity.getName() + "_ProfilePicture";
            String avatarPath = uploadService.uploadFile(picture, filename);
            userEntity.setAvatar(avatarPath);
        }
        userRepository.save(userEntity);
        teacherInfoRepository.save(teacherInfo);
        TeacherResponse response = modelMapper.map(userEntity, TeacherResponse.class);
        response.setBirthday(teacherInfo.getBirthday());
        return  response;
    }
}
