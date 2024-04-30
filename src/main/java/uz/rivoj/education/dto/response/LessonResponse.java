package uz.rivoj.education.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LessonResponse {
    private Integer number;
    private String title;
    private String content;
    private String cover; // content abloshkasi
    private UUID moduleId; // modul numberga o'zgartirish kerakmi ?
}
