package uz.rivoj.education.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthAdmin {
    private UserResponse userResponse;
    private JwtResponse jwtResponse;
}
