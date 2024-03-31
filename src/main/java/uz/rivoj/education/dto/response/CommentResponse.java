package uz.rivoj.education.dto.response;

import lombok.*;
import uz.rivoj.education.entity.LessonEntity;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommentResponse {
    private UUID lessonId;
    private String description;
}
