package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.ChatCR;
import uz.rivoj.education.dto.request.UserCR;
import uz.rivoj.education.dto.response.UserDetailsDTO;
import uz.rivoj.education.dto.response.UserResponse;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.entity.UserRole;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.repository.*;
import uz.rivoj.education.service.firebase.FirebaseService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mee")
@Slf4j
public class MeController {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final FirebaseService firebaseService;

    @GetMapping
    public String getTest() {
        return "woring";
    }

    @PostMapping
    public void postTest() {

        UserCR userDto = new UserCR();
        userDto.setName("admin");
        userDto.setSurname("admin");
        userDto.setPhoneNumber("admin");
        userDto.setPassword("admin");

        try {
            if (userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()) {
                log.info("Admin user already exists");
                return;
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

            firebaseService.createUser(new UserDetailsDTO(String.valueOf(user.getId()), user.getPhoneNumber(), user.getAvatar(), user.getName(), user.getSurname(), String.valueOf(user.getRole())));

            Optional<List<UUID>> optionalTeacherIdes = userRepository.findTeacherIdes(UserRole.TEACHER);
            optionalTeacherIdes.ifPresent(teacherIdes -> teacherIdes.forEach(teacherId -> {
                try {
                    firebaseService.createChat(new ChatCR(String.valueOf(teacherId), String.valueOf(user.getId())), String.valueOf(UUID.randomUUID()));
                } catch (ExecutionException | InterruptedException e) {
                    log.error("Failed to create chat for admin with teacher {}: {}", teacherId, e.getMessage());
                }
            }));

            log.info("Admin user created successfully");
        } catch (Exception e) {
            log.error("Failed to create admin user: {}", e.getMessage());
        }
    }

}
