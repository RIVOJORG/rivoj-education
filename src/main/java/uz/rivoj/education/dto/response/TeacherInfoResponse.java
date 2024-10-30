package uz.rivoj.education.dto.response;


import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TeacherInfoResponse {
    private UUID teacherId;
    private String name;
    private String surname;
    private String avatar;
    private String subject;
    private String about;

}
