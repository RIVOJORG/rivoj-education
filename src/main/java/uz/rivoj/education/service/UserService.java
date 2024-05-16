package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.LoginRequest;
import uz.rivoj.education.dto.request.UserCreateRequest;
import uz.rivoj.education.dto.response.UserResponse;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.exception.WrongPasswordException;
import uz.rivoj.education.repository.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;
//    private final PasswordEncoder passwordEncoder;

    public UserResponse add(UserCreateRequest userDto) {
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setRole(UserRole.STUDENT);
//        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return modelMapper.map(userRepository.save(userEntity), UserResponse.class);
    }

    public UserResponse login(LoginRequest login) {
        UserEntity userEntity = userRepository.findUserEntityByPhoneNumber(login.getPhoneNumber())
                .orElseThrow(
                        () -> new DataNotFoundException("user not found")
                );

        System.out.println("login.getPassword() = " + login.getPassword());
        System.out.println("userEntity.getPassword() = " + userEntity.getPassword());
        if(Objects.equals(login.getPassword(), userEntity.getPassword())) {
            return modelMapper.map(userEntity, UserResponse.class);
        }
        throw new WrongPasswordException("password didn't match");
    }

    public List<UserResponse> getAll() {
        List<UserResponse> list = new ArrayList<>();
        for (UserEntity user : userRepository.findAll()) {
            list.add(modelMapper.map(user, UserResponse.class));
        }
        return list;
    }

    public UserResponse getUser(UUID id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("user not found")
        );
        return modelMapper.map(userEntity, UserResponse.class);
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
        return "Successfully " + status.toString() + "E";
    }
}
