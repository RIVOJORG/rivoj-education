package uz.rivoj.education.dto.response;


import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class TeacherResponse { //
    private String name;
    private String surname;
    private String avatar;
    private String subject;
    private String about;
    private LocalDate birthday;
}
