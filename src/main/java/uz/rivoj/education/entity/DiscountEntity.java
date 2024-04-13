package uz.rivoj.education.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Entity(name = "discount")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DiscountEntity extends BaseEntity {
    private Double percentage; //foiz
    private Integer coin;
}
