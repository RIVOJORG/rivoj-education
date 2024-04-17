package uz.rivoj.education.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "discount")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DiscountEntity extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private UserEntity student;
    private Double percentage; //foiz
    private Integer coin;
}
