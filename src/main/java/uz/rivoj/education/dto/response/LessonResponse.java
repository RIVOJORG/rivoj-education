package uz.rivoj.education.dto.response;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import uz.rivoj.education.entity.ModuleEntity;

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
    private String cover;
    private UUID moduleId;
    private String description;
    private List<String> additionalLinks;
    private List<CommentResponse> comments;
    private TeacherInfoResponse teacherInfo;

}

