package uz.rivoj.education.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TeacherInfoResponse {
    private String name;
    private String surname;
    private String avatar;
    private String subject;
    private String about;

}
