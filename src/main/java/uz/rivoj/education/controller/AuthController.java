package uz.rivoj.education.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.rivoj.education.dto.request.AuthDto;
import uz.rivoj.education.dto.request.TokenRefreshDTO;
import uz.rivoj.education.dto.response.JwtResponse;
import uz.rivoj.education.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    @PostMapping("/sign-in")
    public ResponseEntity<JwtResponse> signIn(@Valid @RequestBody AuthDto request) {
        return ResponseEntity.ok(userService.signIn(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody TokenRefreshDTO request){
        return ResponseEntity.ok(userService.tokenRefresh(request));
    }
}
