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
public class SpecialLessonResponse {
    private UUID id;
    private String title;
    private String cover;
    private String source;
    private String description;
    private UUID teacherId;


}
