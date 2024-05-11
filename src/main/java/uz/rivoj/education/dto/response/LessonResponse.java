package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonResponse {
    private UUID id;
    private Integer lessonNumber;
    private String title;
    private String source;
    private String cover; // content abloshkasi
    private Integer moduleNumber; // id edi numberga o'zagartirildi
    private String description;
}
