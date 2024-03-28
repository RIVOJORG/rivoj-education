package uz.rivoj.education.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {
    private String phoneNumber;
    private String password;
}
