package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.AttendanceCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.service.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {
    private final AttendanceService attendanceService;
    private final UploadService uploadService;
    private final ModuleService moduleService;
    private final NotificationService notificationService;

    @PreAuthorize("permitAll()")
    @PostMapping("/create-attendance")
    public ResponseEntity<AttendanceResponse> createAttendance(@RequestBody AttendanceCR attendance){
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(attendance));
    }
    @PreAuthorize("permitAll()")
    @GetMapping("/get/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.findByAttendanceId(id));
    }
    @PreAuthorize("permitAll()")
    @GetMapping("/getAllLessons/{userId}")
    public ResponseEntity<List<LessonResponse>> getAllLessons(@PathVariable UUID userId, UUID moduleId){
        return ResponseEntity.ok(moduleService.getAllAccessibleLessonsOfUser(userId,moduleId));
    }
    @PreAuthorize("permitAll()")
    @GetMapping("/getAllModules/{userId}")
    public ResponseEntity<List<ModuleResponse>> getAllModules(@PathVariable UUID userId) {
        return  ResponseEntity.ok(moduleService.getAllModules(userId));
    }

    @PostMapping(value = "/upload-homework", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadHomework(
            @ModelAttribute UUID attendanceId,
            @RequestPart("homeworkVideo") MultipartFile homeworkVideo
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadService.uploadFile(homeworkVideo, attendanceId.toString()));
    }

}
