package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rivoj.education.dto.request.ChatCR;
import uz.rivoj.education.dto.request.TeacherCR;
import uz.rivoj.education.dto.request.TeacherUpdate;
import uz.rivoj.education.dto.response.SubjectResponse;
import uz.rivoj.education.dto.response.TeacherResponse;
import uz.rivoj.education.dto.response.UserDetailsDTO;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.SubjectRepository;
import uz.rivoj.education.repository.TeacherInfoRepository;
import uz.rivoj.education.repository.UserRepository;
import uz.rivoj.education.service.firebase.FirebaseService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherInfoRepository teacherInfoRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;
    private final FirebaseService firebaseService;

    @Transactional
    public ResponseEntity<String> createTeacher(TeacherCR teacherCr) {
        Optional<UserEntity> userByPhoneNumber = userRepository.findByPhoneNumber(teacherCr.getPhoneNumber());
        if(userByPhoneNumber.isPresent()){
            throw new DataAlreadyExistsException("Phone number already registered!");
        }
        SubjectEntity subject = subjectRepository.findById(teacherCr.getSubjectId())
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this title: " + teacherCr.getSubjectId()));
        UserEntity user = modelMapper.map(teacherCr, UserEntity.class);
        user.setRole(UserRole.TEACHER);
        user.setUserStatus(UserStatus.UNBLOCK);
        user.setPassword(passwordEncoder.encode(teacherCr.getPassword()));
        TeacherInfo teacher = TeacherInfo.builder()
                .about(teacherCr.getAbout())
                .subject(subject)
                .teacher(user)
                .build();
        UserEntity savedUser = userRepository.save(user);
        teacherInfoRepository.save(teacher);
        
        try {
            firebaseService.createUser(new UserDetailsDTO(String.valueOf(savedUser.getId()),savedUser.getPhoneNumber(),savedUser.getAvatar(),savedUser.getName(),savedUser.getSurname(),String.valueOf(savedUser.getRole())));
            System.out.println("User created");
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create user on Firebase! \n" + e.getMessage());
        }
        Optional<List<UUID>> optionalStudentIdes = userRepository.findStudentIdesBySubjectId(UserRole.STUDENT,teacherCr.getSubjectId());
        System.out.println("optionalStudentIdes.isEmpty() = " + optionalStudentIdes.isEmpty());
        System.out.println("optionalStudentIdes.get().isEmpty() = " + optionalStudentIdes.get().isEmpty());
        if (optionalStudentIdes.isPresent()) {
            optionalStudentIdes.get().forEach(studentId -> {
                System.out.println("studentId = " + studentId);
                try {
                    firebaseService.createChat(
                            new ChatCR(String.valueOf(studentId), String.valueOf(savedUser.getId())),
                            String.valueOf(UUID.randomUUID())
                    );
                    System.out.println("Chat created for student ID: " + studentId);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException("Failed to create chat for student ID: " + studentId, e);
                }
            });
        } else {
            System.out.println("No students found for subject ID: " + teacherCr.getSubjectId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully created!");
    }


    @SneakyThrows
    public TeacherResponse updateProfile(TeacherUpdate teacherUpdate, UUID teacherId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
        UserEntity userEntity = userRepository.findById(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
        if(teacherUpdate.getBirthday() != null){
            userEntity.setBirthday(teacherUpdate.getBirthday());}
        if(teacherUpdate.getSurname() != null){
            userEntity.setSurname(teacherUpdate.getSurname());}
        if(teacherUpdate.getPhoneNumber() != null){
            userEntity.setPhoneNumber(teacherUpdate.getPhoneNumber());}
        if(teacherUpdate.getName() != null){
            userEntity.setName(teacherUpdate.getName());}
        if(teacherUpdate.getPassword() != null){
            userEntity.setPassword(passwordEncoder.encode(teacherUpdate.getPassword()));
        }
        UserEntity save = userRepository.save(userEntity);
        teacherInfoRepository.save(teacherInfo);
        TeacherResponse response = modelMapper.map(userEntity, TeacherResponse.class);
        response.setBirthday(teacherInfo.getTeacher().getBirthday());
        response.setSubject(SubjectResponse.builder().title(teacherInfo.getSubject().getTitle()).id(teacherInfo.getSubject().getId()).build());
        firebaseService.updateUser(new UserDetailsDTO(String.valueOf(save.getId()),save.getPhoneNumber(),save.getAvatar(),save.getName(),save.getSurname(),String.valueOf(save.getRole())));
        return  response;
    }

    public SubjectResponse getSubject(UUID userId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(userId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found!"));
        SubjectEntity subjectEntity = subjectRepository.findById(teacherInfo.getSubject().getId())
                .orElseThrow(() -> new DataNotFoundException("Subject not found!"));
        return modelMapper.map(subjectEntity,SubjectResponse.class);
    }
}
