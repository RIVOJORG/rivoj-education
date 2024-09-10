package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonResponse {
    private UUID id;
    private Integer number;
    private String title;
    private String source;
    private String cover; // content abloshkasi
    private UUID moduleId;
    private String description;
    private List<CommentResponse> comments;

}
