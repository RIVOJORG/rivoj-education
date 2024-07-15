package uz.rivoj.education.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SubjectCR {
    private String title;
    private Integer moduleCount;


}
