package uz.rivoj.education.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DiscountResponse {
    private Double percentage; //foiz
    private Integer coin;
}
