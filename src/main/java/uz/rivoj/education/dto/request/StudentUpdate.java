package uz.rivoj.education.dto.request;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class StudentUpdate {
    private String name;
    private String surname;
    private LocalDate birthday;
}
