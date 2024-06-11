package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.AttendanceRequest;
import uz.rivoj.education.dto.request.MessageCreateRequest;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.dto.response.ChatResponse;
import uz.rivoj.education.dto.response.MessageResponse;
import uz.rivoj.education.dto.response.NotificationResponse;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.service.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {
    private final AttendanceService attendanceService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final ModuleService moduleService;
    private final NotificationService notificationService;

    // ATTENDANCE

    @PostMapping("/create-attendance")
    public ResponseEntity<AttendanceResponse> createAttendance(@RequestBody AttendanceRequest attendance){
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(attendance));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.getAttendance(id));
    }



    // MODULE
    @GetMapping("get-module/{id}")
    public ModuleEntity getModuleById(@PathVariable UUID id) {
        return moduleService.getModule(id);
    }


}
