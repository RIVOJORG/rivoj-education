package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.NotificationRequest;
import uz.rivoj.education.dto.response.NotificationResponse;
import uz.rivoj.education.entity.NotificationEntity;
import uz.rivoj.education.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/create-notification")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest notificationRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(notificationRequest));
    }

    @GetMapping("/get-all")
    public List<NotificationEntity> getAll(){
        return notificationService.getAll();
    }

    @GetMapping("get-notification{id}")
    public NotificationResponse getById(@PathVariable UUID id){
        return notificationService.getById(id);
    }

    @DeleteMapping("delete-notification{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id){
        return ResponseEntity.status(200).body(notificationService.delete(id));
    }
}
