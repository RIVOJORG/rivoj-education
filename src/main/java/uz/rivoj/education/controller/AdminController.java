package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.*;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.dto.update.LessonUpdateDTO;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.service.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final StudentService studentService;
    private final LessonService lessonService;
    private final UserService userService;
    private final TeacherService teacherService;
    private final ProgressService progressService;
    private final AttendanceService attendanceService;
    private final ChatService chatService;
    private final CommentService commentService;
    private final ModuleService moduleService;
    private final NotificationService notificationService;
    private final SubjectService subjectService;
    private final UploadService uploadService;

    @PostMapping("/add-student")
    public ResponseEntity<String> addStudent(@RequestBody StudentCR studentCR){
        return ResponseEntity.status(200).body(studentService.addStudent(studentCR));
    }

    @PostMapping("/add-teacher")
    public ResponseEntity<String> addTeacher(@RequestBody TeacherCR teacherInfo){
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(teacherInfo));
    }
    @PostMapping("/add-admin")
    public ResponseEntity<String> addAdmin(@RequestBody UserCR adminDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addAdmin(adminDto));
    }
    @PostMapping("/create-module")
    public ResponseEntity<ModuleResponse> createModule(@RequestBody ModuleCR createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.create(createRequest));
    }

    @PostMapping("/create-subject")
    public ResponseEntity<SubjectResponse> createSubject(@RequestBody SubjectCR createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(createRequest));
    }
    @PostMapping("/create-notification")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationCR notificationCR){
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(notificationCR));
    }
    @PostMapping("/create-lesson")
    public ResponseEntity<LessonResponse> createLesson(@RequestBody LessonCR createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(createRequest));
    }
    @PutMapping(value = "/lessonVideoUpload",
            consumes = MULTIPART_FORM_DATA_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") final MultipartFile file,UUID lessonId) throws IOException {
        return ResponseEntity.ok(uploadService.uploadFile(file,lessonId));
    }
    @PutMapping("/update-role{userPhoneNumber}")
    public ResponseEntity<String> updateRole(@PathVariable String userPhoneNumber, @RequestParam UserRole userRole){
        return ResponseEntity.status(200).body(userService.updateUser(userPhoneNumber, userRole));
    }
    @PutMapping("/update-lesson{lessonId}")
    public ResponseEntity<String> updateLesson(@PathVariable UUID lessonId,
                                               @RequestBody LessonUpdateDTO updateDTO) {
        return ResponseEntity.status(200).body(lessonService.updateLesson(lessonId, updateDTO));
    }
    @PutMapping("/change-phoneNumber/{oldPhoneNumber}/{newPhoneNumber}")
    public ResponseEntity<String> changePhoneNumber(
            @PathVariable String oldPhoneNumber,
            @PathVariable String newPhoneNumber) {
        return ResponseEntity.status(200).body(userService.changePhoneNumber(oldPhoneNumber, newPhoneNumber));
    }
    @GetMapping("/get-lessons-by-module{moduleId}") // for mentor and admin
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

    @GetMapping("get-lesson/{id}")
    public LessonResponse getLessonById(@PathVariable UUID id) {
        return lessonService.findByLessonId(id);
    }

    @GetMapping("/get-student-full-info{phoneNumber}")
    public ResponseEntity<GetStudentFullInfoResponse> getStudentFullInfoResponse(@PathVariable String phoneNumber){
        return ResponseEntity.status(200).body(progressService.getStudentFullInfoResponse(phoneNumber));
    }

    @GetMapping("/get-all-attendance{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAllUserAttendance(@PathVariable UUID userId){
        return ResponseEntity.ok(attendanceService.getAllUserAttendance(userId));
    }

    @GetMapping("/get-attendance/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.findByAttendanceId(id));
    }

    @GetMapping("/get-attendance-by-status") // for mentor and admin
    public List<AttendanceResponse> getAllAttendanceByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam AttendanceStatus status){
        return attendanceService.getAllAttendanceByStatus(page, size, status);
    }

    @GetMapping("get-comment/{id}")
    public CommentResponse getCommentById(@PathVariable UUID id) {
        return commentService.findByCommentId(id);
    }

    @GetMapping("get-module/{id}")
    public ModuleResponse getModuleById(@PathVariable UUID id) {
        return moduleService.findByModuleId(id);
    }

    @GetMapping("/get-all-notification")
    public List<NotificationResponse> getAllNotification(){
        return notificationService.getAll();
    }

    @GetMapping("get-notification{id}")
    public NotificationResponse getNotificationById(@PathVariable UUID id){
        return notificationService.getById(id);
    }

    @GetMapping("/get-all-subjects")
    public List<SubjectResponse> getAllSubject(){
        return subjectService.getAll();
    }

    @GetMapping("get-subject/{id}")
    public String getSubjectById(@PathVariable UUID id) {
        return subjectService.findBySubjectId(id);
    }

    @GetMapping("/block-unblock-user{phoneNumber}")
    public ResponseEntity<String> blockUnblockUser(@PathVariable String phoneNumber, @RequestParam UserStatus status){
        return ResponseEntity.status(200).body(userService.blockUnblockUser(phoneNumber, status));
    }

    @GetMapping("/get-all-users")
    public List<UserResponse> getAll(){
        return userService.getAll();
    }

    @GetMapping("get-user/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/delete-subject{subjectId}")
    public ResponseEntity<String> deleteSubject(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(subjectService.delete(subjectId));
    }
    @DeleteMapping("delete-notification{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id){
        return ResponseEntity.status(200).body(notificationService.delete(id));
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
}
