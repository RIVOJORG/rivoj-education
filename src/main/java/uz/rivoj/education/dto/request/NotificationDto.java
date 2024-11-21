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
    private List<String> topic;
    private Map<String, String> data;


    }