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
public class ModuleResponse {
    private UUID module_id;
    private Integer moduleNumber;
    private String subject;
    private List<LessonResponse> lessons;

}
