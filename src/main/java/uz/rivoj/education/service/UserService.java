package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.AuthDto;
import uz.rivoj.education.dto.request.UserCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;
import uz.rivoj.education.service.jwt.JwtUtil;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public String add(UserCR dto) {
        Optional<UserEntity> userEntity = userRepository.findUserEntityByPhoneNumber(dto.getPhoneNumber());
        if(userEntity.isPresent()) {
            throw  new DataAlreadyExistsException("User already exists");
        }
        UserEntity map = modelMapper.map(dto, UserEntity.class);
        map.setPassword(passwordEncoder.encode(map.getPassword()));
        userRepository.save(map);
        return "Successfully signed up";
    }


    public JwtResponse signIn(AuthDto dto) {
        UserEntity user = userRepository.findUserEntityByPhoneNumber(dto.getPhoneNumber())
                .orElseThrow(() -> new DataNotFoundException("user not found"));
      if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return new JwtResponse(jwtUtil.generateToken(user));
        }
        throw new AuthenticationCredentialsNotFoundException("password didn't match");
    }

    public String addAdmin(UserCR userDto) {
        if(userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()) {
            throw  new DataAlreadyExistsException("User already exists");
        }
        UserEntity user = UserEntity.builder()
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .phoneNumber(userDto.getPhoneNumber())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(UserRole.ADMIN)
                .userStatus(UserStatus.UNBLOCK)
                .build();
        UserResponse userResponse = modelMapper.map(userRepository.save(user), UserResponse.class);
        userResponse.setId(user.getId());
        return "Created";
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
