package uz.rivoj.education.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StudentResponse {
    private UUID id;
    private String name;
    private String surname;
    private String avatar;
    private String phoneNumber;
    private LocalDate birth;
}
