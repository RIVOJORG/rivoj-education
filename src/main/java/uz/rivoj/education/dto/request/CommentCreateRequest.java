package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommentCreateRequest {
    private UUID lessonId;
    private UUID ownerId;
    private String description;
}
