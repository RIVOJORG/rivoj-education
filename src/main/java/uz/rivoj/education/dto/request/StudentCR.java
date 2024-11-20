package uz.rivoj.education.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class StudentCR {
    private String name;
    private String surname;
    private String phoneNumber;
    private String password;
    private LocalDate birthday;
    private UUID subjectId;
    private UUID starterModuleId;
}
