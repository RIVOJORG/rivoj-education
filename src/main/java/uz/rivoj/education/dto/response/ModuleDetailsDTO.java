package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ModuleDetailsDTO {
    private UUID moduleId;
    private Integer moduleNumber;
}
