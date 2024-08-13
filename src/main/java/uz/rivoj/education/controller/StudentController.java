package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.AttendanceCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.service.*;

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
    public ResponseEntity<AttendanceResponse> createAttendance(@RequestBody AttendanceCR attendance){
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(attendance));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.findByAttendanceId(id));
    }




//    @GetMapping("getCurrentModule/{userId}")
//    public ModuleResponse getCurrentModule(@PathVariable UUID userId) {
//        return moduleService.getCurrentModule(userId);
//    }

    @GetMapping("getAllModules/{userId}")
    public ResponseEntity<List<ModuleResponse>> getAllModules(@PathVariable UUID userId) {
        return  ResponseEntity.ok(moduleService.getAllModules(userId));
    }


}
