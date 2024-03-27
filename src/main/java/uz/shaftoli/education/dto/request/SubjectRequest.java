package uz.shaftoli.education.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SubjectRequest {
    private String title;
    private UUID module_id;
}
