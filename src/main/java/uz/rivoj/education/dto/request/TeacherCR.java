package uz.rivoj.education.dto.request;


import lombok.*;
import uz.rivoj.education.entity.SubjectEntity;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TeacherCR {
    private String name;
    private String surname;
    private String phoneNumber;
    private String password;
    private UUID subjectId;
    private String about;
}
