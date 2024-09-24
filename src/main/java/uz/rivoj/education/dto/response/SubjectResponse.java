package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SubjectResponse {
    private String title;
    private UUID subjectId;
}
