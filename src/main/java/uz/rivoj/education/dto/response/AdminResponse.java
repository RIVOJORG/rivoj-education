package uz.rivoj.education.dto.response;


import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AdminResponse {
    private String name;
    private String surname;
    private String avatar;
    private LocalDate birth;
    private String phoneNumber;
}
