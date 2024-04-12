package uz.rivoj.education.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BestStudentResponse {
    private String avatar;
    private String name;
    private String surname;
    private Integer percentage;
}
