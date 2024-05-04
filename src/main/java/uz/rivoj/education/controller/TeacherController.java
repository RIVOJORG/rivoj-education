package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.dto.update.CheckAttendanceDTO;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.service.*;

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

    //    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-all-student")
    public ResponseEntity<List<StudentResponse>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(200).body(studentService.getAll(page, size));
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

    @GetMapping("/get-attendances-by-lesson")
    public ResponseEntity<List<AttendanceResponse>> getAttendancesByLesson(@PathVariable UUID lessonId){
        return ResponseEntity.ok(attendanceService.getAttendancesByLesson(lessonId));
    }

//    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-lessons-by-module") // for mentor and admin
    public List<LessonResponse> getLessonsByModule(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID moduleId){
        return lessonService.getLessonsByModule(page, size, moduleId);
    }

    @GetMapping("/get-my-chats") // hammada bo'ladi bu API student, admin ham o'zini chatlarini olishi mumkun
    public List<ChatResponse> getMyChats(@PathVariable UUID memberId){
        return chatService.getMyChats(memberId);
    }

    @GetMapping("/get-messages") // hammada bo'ladi bu API student, admin ham o'zini message larini olishi mumkun
    public List<MessageResponse> getMessages(@PathVariable UUID chatId){
        return messageService.getMessages(chatId);
    }
}