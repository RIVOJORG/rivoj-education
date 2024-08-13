package uz.rivoj.education.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthTeacher {
    private TeacherResponse teacherResponse;
    private JwtResponse jwtResponse;
}
