package uz.rivoj.education.dto.request;

import lombok.*;

import java.util.List;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class NotificationDto {
    private String title;
    private String body;
    private List<String> destinations;
    private Map<String, String> data;
    private boolean isTopic;


    }