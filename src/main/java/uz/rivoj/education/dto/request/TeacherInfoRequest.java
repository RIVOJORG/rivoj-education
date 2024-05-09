package uz.rivoj.education.dto.request;


import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TeacherInfoRequest {
    private String name;
    private String surname;
    private String phoneNumber;
    private String password;
    private UUID teacher;
    private String subject;
    private String about;
}
