package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.AttendanceCR;
import uz.rivoj.education.dto.request.StudentUpdate;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.service.*;

import java.io.IOException;
import java.security.Principal;
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
    private  final StudentService studentService;

    @PostMapping("/create-attendance")
    public ResponseEntity<AttendanceResponse> createAttendance(@RequestBody AttendanceCR attendance, Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(attendance, UUID.fromString(principal.getName())));
    }
    @GetMapping("/getAllAttendance")
    public ResponseEntity<AttendanceResponse> getAttendanceById(Principal principal) {
        return ResponseEntity.ok(attendanceService.findByAttendanceId(UUID.fromString(principal.getName())));
    }
    @GetMapping("/getAllLessonsOfModule")
    public ResponseEntity<List<LessonResponse>> getAllLessons(Principal principal,@RequestParam UUID moduleId){
        return ResponseEntity.ok(moduleService.getAllAccessibleLessonsOfUser(UUID.fromString(principal.getName()),moduleId));
    }
    @GetMapping("/getAllModules")
    public ResponseEntity<List<ModuleResponse>> getAllModules(Principal principal) {
        return  ResponseEntity.ok(moduleService.getAllModules(UUID.fromString(principal.getName())));
    }

    @PostMapping(value = "/upload-homework", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadHomework(
            @RequestParam UUID studentId,
            @RequestPart("HomeworkFile") MultipartFile homeworkVideo
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadService.uploadFile(homeworkVideo, studentId.toString()));
    }
    @PutMapping(value = "/update_profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentResponse> createComment(
            @ModelAttribute StudentUpdate studentUpdate,
            Principal principal,
            @RequestPart("ProfilePicture") MultipartFile picture){
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.updateProfile(studentUpdate,picture, UUID.fromString(principal.getName())));
    }
}
