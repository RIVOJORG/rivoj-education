package uz.rivoj.education.dto.response;


import lombok.*;

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
}
