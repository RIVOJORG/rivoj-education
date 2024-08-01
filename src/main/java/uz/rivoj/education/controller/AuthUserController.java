package uz.rivoj.education.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.rivoj.education.dto.request.LoginRequest;
import uz.rivoj.education.dto.response.UserResponse;
import uz.rivoj.education.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthUserController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<UserResponse> signIn(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

   @PostMapping("/sign-up")
   public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
       return ResponseEntity.status(201).body(userService.add(request));
   }
}
