package uz.shaftoli.education.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginDTO {
    private String phoneNumber;
    private String password;
}
