package uz.rivoj.education.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtResponse> signIn(@Valid @RequestBody AuthDto request) {
        log.info("Authentication attempt for user: {}", request.getPhoneNumber());
        try {
            JwtResponse response = userService.signIn(request);
            log.info("User authenticated successfully: {}", request.getPhoneNumber());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", request.getPhoneNumber(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody TokenRefreshDTO request) {
        log.info("Token refresh request received");
        try {
            JwtResponse response = userService.tokenRefresh(request);
            log.info("Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw e;
        }
    }
}
