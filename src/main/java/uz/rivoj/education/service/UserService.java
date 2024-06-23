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

    public UserResponse add(UserCreateRequest userDto, UserRole userRole) {
        UserEntity user = userRepository.findByPhoneNumber(userDto.getPhoneNumber()).orElseThrow(
                () -> new DataNotFoundException("User not found"));
        user.setPassword(userDto.getPassword());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setName(userDto.getName());
        user.setRole(userRole);
//        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserResponse userResponse = modelMapper.map(userRepository.save(user), UserResponse.class);
        userResponse.setId(user.getId());
        return userResponse;
    }

    public UserResponse login(LoginRequest login) {
        UserEntity userEntity = userRepository.findUserEntityByPhoneNumber(login.getPhoneNumber())
                .orElseThrow(
                        () -> new DataNotFoundException("user not found"));
        if(Objects.equals(login.getPassword(), userEntity.getPassword())) {
            UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);
            userResponse.setId(userEntity.getId());
            return userResponse;
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
