package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SubjectCreateRequest {
    private String name; // subject
    private String table_of_contents;
    private Integer moduleCount;


}
