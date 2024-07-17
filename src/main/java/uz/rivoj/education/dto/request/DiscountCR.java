package uz.rivoj.education.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DiscountCR {
    private Double percentage; //foiz
    private Integer coin;
}
