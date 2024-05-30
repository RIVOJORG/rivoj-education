package uz.rivoj.education.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.rivoj.education.dto.request.LoginRequest;
import uz.rivoj.education.dto.request.UserCreateRequest;
import uz.rivoj.education.dto.response.UserResponse;
import uz.rivoj.education.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    @PostMapping("/sign-up")
    public UserResponse signUp(
            @Valid
            @RequestBody UserCreateRequest userDto
    ) {
        return userService.add(userDto);
    }
    // bu method bo'lmasligi kerak menimcha Nodirbek. Bizda SignUp yoq'ku. Admin controllerda yozib qoydim addStudentni


}