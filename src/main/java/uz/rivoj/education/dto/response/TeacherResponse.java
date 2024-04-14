package uz.rivoj.education.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class TeacherResponse {
    private String name;
    private String surname;
    private String avatar;
    private SubjectResponse subject;
    private String about;
}
