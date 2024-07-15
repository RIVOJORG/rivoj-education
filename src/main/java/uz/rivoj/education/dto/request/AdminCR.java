package uz.rivoj.education.dto.request;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AdminCR {
    private String name;
    private String surname;
    private String phoneNumber;
    private String password;
}
