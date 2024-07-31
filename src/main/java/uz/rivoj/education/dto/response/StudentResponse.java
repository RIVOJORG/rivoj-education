package uz.rivoj.education.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StudentResponse {
    private String name;
    private String surname;
    private String avatar;
    private String phoneNumber;

}
