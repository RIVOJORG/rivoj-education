package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.LessonCR;
import uz.rivoj.education.dto.request.TeacherUpdate;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.dto.update.CheckAttendanceDTO;
import uz.rivoj.education.dto.update.LessonUpdateDTO;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.service.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final LessonService lessonService;
    private final ChatService chatService;
    private final ModuleService moduleService;
    private final TeacherService teacherService;


    @GetMapping("/get-attendance-by-status")
    public List<AttendanceResponse> getAllAttendanceByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam AttendanceStatus status){
        return attendanceService.getAllAttendanceByStatus(page, size, status);
    }

    @PutMapping("/check-attendance")
    public ResponseEntity<String> checkAttendance(@RequestBody CheckAttendanceDTO checkAttendanceDTO,Principal principal) {
        return ResponseEntity.status(200).body(attendanceService.checkAttendance(checkAttendanceDTO,UUID.fromString(principal.getName())));
    }

    @GetMapping("/get-all-attendance-by-userId{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAllUserAttendance(@PathVariable UUID userId){
        return ResponseEntity.ok(attendanceService.getAllUserAttendance(userId));
    }
    @GetMapping("/get-attendance/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.findByAttendanceId(id));
    }

    @PutMapping(value = "/update_profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherResponse> createComment(
            @ModelAttribute TeacherUpdate teacherUpdate,
            Principal principal,
            @RequestPart(value = "ProfilePicture",required = false) MultipartFile picture){
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.updateProfile(teacherUpdate,picture, UUID.fromString(principal.getName())));
    }
    @PostMapping("/create-module")
    public ResponseEntity<ModuleResponse> createModule(@RequestBody Integer moduleNumber, Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.create(moduleNumber, UUID.fromString(principal.getName())));
    }

    @GetMapping("/getAllLessonsByModule/{moduleId}")
    public ResponseEntity<List<LessonResponse>> getAllLessonsByModule(@PathVariable UUID moduleId){
        return ResponseEntity.status(200).body(moduleService.getAllLessonsByModule(moduleId));
    }
    @GetMapping("/getUncheckedAttendanceResponse{attendanceId}")
    public ResponseEntity<UncheckedAttendanceResponse> getUncheckedAttendanceResponse(@PathVariable UUID attendanceId){
        return ResponseEntity.status(200).body(attendanceService.getUncheckedAttendanceResponse(attendanceId));
    }

    @GetMapping("/get-unchecked-attendances-by-subjectId{subjectId}")
    public ResponseEntity<List<AttendanceResponse>> getUncheckedAttendancesBySubjectId(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(attendanceService.getUncheckedAttendancesBySubjectId(subjectId));
    }
}
