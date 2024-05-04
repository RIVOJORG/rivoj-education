package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.TeacherInfoRequest;
import uz.rivoj.education.dto.response.UserResponse;
import uz.rivoj.education.entity.TeacherInfo;
import uz.rivoj.education.service.UserService;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;



    @GetMapping("/get-all")
    public List<UserResponse> getAll(){
        return userService.getAll();
    }

    @GetMapping("get-user/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUser(id);
    }
}
