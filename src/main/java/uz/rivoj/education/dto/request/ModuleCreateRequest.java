package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ModuleCreateRequest {
    private Integer number;
    private UUID subjectId;
}
