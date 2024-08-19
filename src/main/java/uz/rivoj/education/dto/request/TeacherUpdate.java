package uz.rivoj.education.dto.request;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class TeacherUpdate {
    private String name;
    private String surname;
    private String phoneNumber;
    private String password;
    private LocalDate birthday;
}
