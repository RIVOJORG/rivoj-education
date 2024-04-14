package uz.rivoj.education.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DiscountRequest {
    private Double percentage; //foiz
    private Integer coin;
}
