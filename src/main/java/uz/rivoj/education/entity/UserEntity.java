package uz.rivoj.education.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity extends BaseEntity {
    private String name;
    private String surname;
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
