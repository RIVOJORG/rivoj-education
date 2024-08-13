package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ModuleResponse {
    private UUID module_id;
    private Integer modulNumber;
    private String subject;
}
