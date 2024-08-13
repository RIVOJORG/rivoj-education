package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.dto.update.CheckAttendanceDTO;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.service.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final LessonService lessonService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final CommentService commentService;
    private final ModuleService moduleService;

    //  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-all-student")
    public ResponseEntity<List<StudentResponse>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(200).body(studentService.getAll(page, size));
    }


    // ATTENDANCE
    @DeleteMapping("/delete-attendance{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable UUID id){
        return ResponseEntity.status(200).body(attendanceService.delete(id));
    }

    @GetMapping("/get-attendance-by-status") // for mentor and admin
    public List<AttendanceResponse> getAllAttendanceByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam AttendanceStatus status){
        return attendanceService.getAllAttendanceByStatus(page, size, status);
    }

    @PutMapping("/check-attendance")
    public ResponseEntity<String> checkAttendance(@RequestBody CheckAttendanceDTO checkAttendanceDTO) {
        return ResponseEntity.status(200).body(attendanceService.checkAttendance(checkAttendanceDTO));
    }

    @GetMapping("/get-all-attendance-by-userId{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAllUserAttendance(@PathVariable UUID userId){
        return ResponseEntity.ok(attendanceService.getAllUserAttendance(userId));
    }
    @GetMapping("/get-attendance/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.findByAttendanceId(id));
    }


    // CHAT

    @GetMapping("get-chat/{id}")
    public ChatEntity getChatById(@PathVariable UUID id) {
        return chatService.getChat(id);
    }

    // LESSON
    //    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-lessons-by-module{moduleId}") // for mentor and admin
    public List<LessonResponse> getLessonsByModule(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID moduleId){
        return lessonService.getLessonsByModule(page, size, moduleId);
    }
    @GetMapping("get-lesson/{id}")
    public LessonResponse getLessonById(@PathVariable UUID id) {
        return lessonService.findByLessonId(id);
    }

    // MODULE

    @GetMapping("get-module/{id}")
    public ModuleResponse getModuleById(@PathVariable UUID id) {
        return moduleService.findByModuleId(id);
    }


//    @GetMapping("/statistics")
//    public ResponseEntity<List<StudentStatisticsDTO>> getStudentStatistics(
//            Principal principal,
//            @RequestParam Integer moduleNumber) {
//
//        System.out.println("principal = " + principal);
//        System.out.println("principal.getName() = " + principal.getName());
//        return ResponseEntity.ok(studentService.getStudentStatistics(principal.getName(), moduleNumber));
//    }


}
