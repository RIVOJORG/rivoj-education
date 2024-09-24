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
public class ModuleDTO {
    private UUID module_id;
    private Integer moduleNumber;
    private List<LessonResponse> lesson;

}
