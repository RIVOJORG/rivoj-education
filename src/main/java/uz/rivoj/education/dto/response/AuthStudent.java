package uz.rivoj.education.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthStudent {
    private StudentResponse studentResponse;
    private JwtResponse jwtToken;
}
