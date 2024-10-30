package uz.rivoj.education.dto.response;

import jakarta.persistence.*;
import lombok.*;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.entity.TeacherInfo;

import java.time.LocalTime;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonResponse {
    private UUID id;
    private UUID teacherId;
    private Integer number;
    private String title;
    private String source;
    private String cover;
    private String description;
    private List<String> additionalLinks;
    private TeacherInfoResponse teacherInfo;


}

