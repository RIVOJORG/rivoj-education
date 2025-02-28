package uz.rivoj.education.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDetailsDTO {
    private String userId;
    private String phoneNumber;
    private String avatar;
    private String name;
    private String surname;
    private String UserRole;
}
