package uz.rivoj.education.dto.request;

import lombok.*;
import uz.rivoj.education.entity.SubjectEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class StudentCreateRequest {
    private String name;
    private String surname;
    private String phoneNumber;
    private String password;
    private LocalDate birthday;
    private String subject;
}
