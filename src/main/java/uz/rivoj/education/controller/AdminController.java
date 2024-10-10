package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.*;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.service.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final StudentService studentService;
    private final LessonService lessonService;
    private final UserService userService;
    private final TeacherService teacherService;
    private final AttendanceService attendanceService;
    private final ChatService chatService;
    private final CommentService commentService;
    private final ModuleService moduleService;
    private final NotificationService notificationService;
    private final SubjectService subjectService;


    @PostMapping("/add-student")
    public ResponseEntity<String> addStudent(@RequestBody StudentCR studentCR){
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.addStudent(studentCR));
    }
    @PostMapping("/add-teacher")
    public ResponseEntity<String> addTeacher(@RequestBody TeacherCR teacherInfo){
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(teacherInfo));
    }
    @PostMapping("/add-admin")
    public ResponseEntity<String> addAdmin(@RequestBody UserCR adminDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addAdmin(adminDto));
    }

    @PostMapping("/create-subject")
    public ResponseEntity<SubjectResponse> createSubject(@RequestBody SubjectCR createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(createRequest));
    }
    @PostMapping("/create-notification")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationCR notificationCR){
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(notificationCR));
    }
    @PostMapping("/create-module")
    public ResponseEntity<ModuleResponse> createModule(@RequestBody Integer moduleNumber, Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.create(moduleNumber, UUID.fromString(principal.getName())));
    }

    @PutMapping("/update-role{userPhoneNumber}")
    public ResponseEntity<String> updateRole(@PathVariable String userPhoneNumber, @RequestParam UserRole userRole){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userPhoneNumber, userRole));
    }

    @PutMapping("/change-phoneNumber/{userId}/{newPhoneNumber}")
    public ResponseEntity<String> changePhoneNumber(
            @PathVariable UUID userId,
            @PathVariable String newPhoneNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePhoneNumber(userId, newPhoneNumber));
    }
    @PutMapping("/change-password/{userId}/{newPassword}")
    public ResponseEntity<String> changePassword(
            @PathVariable UUID userId,
            @PathVariable String newPassword) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePassword(userId,newPassword));
    }
    @GetMapping("/get-lessons-by-module{moduleId}")
    public List<LessonResponse> getLessonsByModule(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID moduleId){
        return lessonService.getLessonsByModule(page, size, moduleId);
    }
    @GetMapping("/get-all-student")
    public ResponseEntity<List<StudentResponse>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(200).body(studentService.getAll(page, size));
    }
    @GetMapping("/get-all-users-byRole")
    public ResponseEntity<Map<String, Object>> getAllByRole(
            @RequestParam UserRole role,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(userService.getAllByRole(role, pageNumber, pageSize));
    }

    @GetMapping("get-lesson/{id}")
    public LessonResponse getLessonById(@PathVariable UUID id) {
        return lessonService.findByLessonId(id);
    }


    @GetMapping("/get-all-subjects")
    public List<SubjectResponse> getAllSubject(){
        return subjectService.getAll();
    }

    @PostMapping("/block-unblock-user{userId}")
    public ResponseEntity<String> blockUnblockUser(@PathVariable UUID userId, @RequestParam UserStatus status){
        return ResponseEntity.status(200).body(userService.blockUnblockUser(userId, status));
    }


    @DeleteMapping("/delete-subject{subjectId}")
    public ResponseEntity<String> deleteSubject(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(subjectService.delete(subjectId));
    }

    @DeleteMapping("/delete-module{moduleId}")
    public ResponseEntity<String> deleteModule(@PathVariable UUID moduleId){
        return ResponseEntity.status(200).body(moduleService.delete(moduleId));
    }
    @DeleteMapping("/delete-attendance{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable UUID id){
        return ResponseEntity.status(200).body(attendanceService.delete(id));
    }
    @DeleteMapping("/delete-chat")
    public ResponseEntity<String> deleteChat(UUID chatId){
        return ResponseEntity.ok(chatService.deleteChat(chatId));
    }

    @DeleteMapping("/delete-comment{commentId}") // for admin
    public ResponseEntity<String> deleteComment(@PathVariable UUID commentId){
        return ResponseEntity.status(200).body(commentService.delete(commentId));
    }
    @DeleteMapping("/delete-lesson{lessonId}")
    public ResponseEntity<String> deleteLesson(@PathVariable UUID lessonId){
        return ResponseEntity.status(200).body(lessonService.delete(lessonId));
    }


    @GetMapping("/getAllModulesOfSubject{subjectId}")
    public ResponseEntity<List<ModuleResponse>> getAllModulesOfSubject(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(moduleService.getAllModulesOfSubject(subjectId));
    }

    @GetMapping("/getStatisticsByModule")
    public ResponseEntity<List<StudentStatisticsDTO>> getStudentStatisticsByModule(
            @RequestParam UUID moduleId
    ) {
        return ResponseEntity.ok(studentService.getStudentStatisticsByModule(moduleId));
    }

    @GetMapping("/getAllStatisticsOnCurrentModule")
    public ResponseEntity<List<StudentStatisticsDTO>> getAllStudentStatisticsOnCurrentModule(
            @RequestParam UUID subjectId
    ) {
        return ResponseEntity.ok(studentService.getAllStudentStatisticsOnCurrentModule2(subjectId));
    }


}
