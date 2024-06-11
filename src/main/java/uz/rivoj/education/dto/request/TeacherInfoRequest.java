package uz.rivoj.education.dto.request;


import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TeacherInfoRequest {
    private UUID userId;
    private String subject;
    private String about;
}
