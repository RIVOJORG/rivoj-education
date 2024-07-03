package uz.rivoj.education.dto.response;


import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserResponse {
    private UUID id;
    private String name;
    private String surname;
    private String avatar;
    private LocalDate birth;
    private String phoneNumber;
    private String password;
    private String token;
}
