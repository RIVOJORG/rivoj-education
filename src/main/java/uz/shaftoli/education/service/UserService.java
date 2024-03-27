package uz.shaftoli.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.shaftoli.education.dto.request.LoginDto;
import uz.shaftoli.education.dto.request.UserCreateDTO;
import uz.shaftoli.education.entity.UserEntity;
import uz.shaftoli.education.entity.UserRole;
import uz.shaftoli.education.exception.DataNotFoundException;
import uz.shaftoli.education.exception.WrongPasswordException;
import uz.shaftoli.education.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserEntity add(UserCreateDTO userDto) {
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setRole(UserRole.STUDENT);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userRepository.save(userEntity);
    }

    public UserEntity login(LoginDto login) {
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
