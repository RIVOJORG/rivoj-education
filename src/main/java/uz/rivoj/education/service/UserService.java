package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.LoginRequest;
import uz.rivoj.education.dto.request.UserCreateRequest;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.entity.UserRole;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.exception.WrongPasswordException;
import uz.rivoj.education.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserEntity add(UserCreateRequest userDto) {
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setRole(UserRole.STUDENT);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userRepository.save(userEntity);
    }

    public UserEntity login(LoginRequest login) {
        UserEntity userEntity = userRepository.findUserEntityByPhoneNumber(login.getPhoneNumber())
                .orElseThrow(
                        () -> new DataNotFoundException("user not found")
                );

        if(passwordEncoder.matches(login.getPassword(), userEntity.getPassword())) {
            return userEntity;
        }
        throw new WrongPasswordException("password didn't match");
    }
}
