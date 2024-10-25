package uz.rivoj.education.dto.response;


import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import uz.rivoj.education.entity.UserRole;
import uz.rivoj.education.entity.enums.UserStatus;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class TeacherResponse { //
    private UUID id;
    private String name;
    private String surname;
    private String avatar;
    private SubjectResponse subject;
    private String about;
    private LocalDate birthday;
    private UserStatus status;
    private String phoneNumber;


}
