package uz.rivoj.education.dto.response;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SpecialAttendanceResponse {
    private Integer score;
}
