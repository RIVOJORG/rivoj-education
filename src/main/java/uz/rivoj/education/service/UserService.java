package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.LoginRequest;
import uz.rivoj.education.dto.request.UserCreateRequest;
import uz.rivoj.education.dto.response.UserResponse;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.exception.WrongPasswordException;
import uz.rivoj.education.repository.*;
import uz.rivoj.education.service.jwt.JwtService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final SubjectRepository subjectRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse add(UserCreateRequest userDto) {
        if(userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()) {
            throw  new DataAlreadyExistsException("User already exists");
        }
        UserEntity user = UserEntity.builder()
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .phoneNumber(userDto.getPhoneNumber())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(UserRole.STUDENT)
                .build();
        UserResponse userResponse = modelMapper.map(userRepository.save(user), UserResponse.class);
        userResponse.setId(user.getId());
        return userResponse;
    }

    public UserResponse login(LoginRequest login) {
        UserEntity user = userRepository.findUserEntityByPhoneNumber(login.getPhoneNumber())
                .orElseThrow(
                        () -> new DataNotFoundException("user not found"));
        Optional<StudentInfo> studentInfo = studentInfoRepository.findStudentInfoByStudentId(user.getId());
        if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            UserResponse response =  UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .phoneNumber(user.getPhoneNumber())
                    .token(jwtService.generateToken(user))
                    .build();
            if(studentInfo.isPresent()){
                response.setAvatar(studentInfo.get().getAvatar());
                response.setBirth(studentInfo.get().getBirthday());
            }
            return response;
        }
        throw new WrongPasswordException("password didn't match");
    }

    public List<UserResponse> getAll() {
        List<UserResponse> list = new ArrayList<>();
        for (UserEntity user : userRepository.findAll()) {
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            userResponse.setId(user.getId());
            list.add(userResponse);
        }
        return list;
    }

    public UserResponse getUser(UUID id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("user not found")
        );
        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);
        userResponse.setId(userEntity.getId());
        return userResponse;
    }

    public UserEntity getUserByPhoneNumber(String phoneNumber){
        Optional<UserEntity> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isEmpty()) {
            throw new DataNotFoundException("User not found with this phone number: " + phoneNumber);
        }
        return userOptional.get();
    }

    public String changePhoneNumber(String oldPhoneNumber, String newPhoneNumber) {
        UserEntity user = getUserByPhoneNumber(oldPhoneNumber);
        user.setPhoneNumber(newPhoneNumber);
        userRepository.save(user);
        return "Phone number successfully updated for user: " + user.getName();
    }

    public String blockUnblockUser(String phoneNumber, UserStatus status) {
        UserEntity user = getUserByPhoneNumber(phoneNumber);
        user.setUserStatus(status);
        userRepository.save(user);
        return "Successfully " + status.toString() + "ED";
    }

    public String updateUser(String userPhoneNumber, UserRole userRole) {
        UserEntity user = userRepository.findByPhoneNumber(userPhoneNumber).orElseThrow(
                () -> new DataNotFoundException("User not found"));
        user.setRole(userRole);
        userRepository.save(user);
        return "Successfully updated";
    }
}
