package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TeacherDTO {
    private String name;
    private String surname;
    private UUID id;
}
