package uz.rivoj.education.dto.response;


import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime birth;
    private String phoneNumber;
    private String password;
}
