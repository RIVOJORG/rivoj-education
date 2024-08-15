package uz.rivoj.education.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthDto {
    private String phoneNumber;
    private String password;
}
