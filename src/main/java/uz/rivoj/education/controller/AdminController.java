package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.*;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.dto.update.LessonUpdateDTO;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.service.*;

import java.util.List;
import java.util.UUID;

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
    private final MessageService messageService;
    private final ModuleService moduleService;
    private final NotificationService notificationService;
    private final SubjectService subjectService;

    // TEACHER
    @PostMapping("/create-teacher")
    public ResponseEntity<String> createTeacher(@RequestBody TeacherInfoRequest teacherInfo){
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(teacherInfo));
    }

    // STUDENT

    //    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-all-student")
    public ResponseEntity<List<StudentResponse>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(200).body(studentService.getAll(page, size));
    }


    @PostMapping("/add-student")
    public ResponseEntity<String> addStudent(StudentCreateRequest studentCreateRequest){
        return ResponseEntity.status(200).body(studentService.addStudent(studentCreateRequest));
    }

    @GetMapping("/get-student-full-info{phoneNumber}")
    public ResponseEntity<GetStudentFullInfoResponse> getStudentFullInfoResponse(@PathVariable String phoneNumber){
        return ResponseEntity.status(200).body(progressService.getStudentFullInfoResponse(phoneNumber));

    }
    @PutMapping("/change-phoneNumber/{oldPhoneNumber}/{newPhoneNumber}")
    public ResponseEntity<String> changePhoneNumber(
            @PathVariable String oldPhoneNumber,
            @PathVariable String newPhoneNumber) {
        return ResponseEntity.status(200).body(userService.changePhoneNumber(oldPhoneNumber, newPhoneNumber));
    }


    // ATTENDANCE
    @DeleteMapping("/delete-attendance{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable UUID id){
        return ResponseEntity.status(200).body(attendanceService.delete(id));
    }

    @GetMapping("/get-all-attendance{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAllUserAttendance(@PathVariable UUID userId){
        return ResponseEntity.ok(attendanceService.getAllUserAttendance(userId));
    }

    @GetMapping("/get-attendance/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.getAttendance(id));
    }

    @GetMapping("/get-attendance-by-status") // for mentor and admin
    public List<AttendanceResponse> getAllAttendanceByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam AttendanceStatus status){
        return attendanceService.getAllAttendanceByStatus(page, size, status);
    }

    // CHAT
    @GetMapping("/get-my-chats{memberId}") // hammada bo'ladi bu API student, admin ham o'zini chatlarini olishi mumkun
    public List<ChatResponse> getMyChats(@PathVariable UUID memberId){
        return chatService.getMyChats(memberId);
    }
    @DeleteMapping("/delete-chat")
    public ResponseEntity<String> deleteChat(UUID chatId){
        return ResponseEntity.ok(chatService.deleteChat(chatId));
    }

    @GetMapping("get-chat/{id}")
    public ChatEntity getChatById(@PathVariable UUID id) {
        return chatService.getChat(id);
    }

    // COMMENT
    @DeleteMapping("/delete-comment{commentId}") // for admin
    public ResponseEntity<String> deleteComment(@PathVariable UUID commentId){
        return ResponseEntity.status(200).body(commentService.delete(commentId));
    }


    @GetMapping("/get-all-comment")
    public List<CommentResponse> getAllComment(){
        return commentService.getAll();
    }

    @GetMapping("get-comment/{id}")
    public CommentEntity getCommentById(@PathVariable UUID id) {
        return commentService.getComment(id);
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

    @PostMapping("/create-lesson")
    public ResponseEntity<LessonResponse> createLesson(@RequestBody LessonCreateRequest createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(createRequest));
    }

    @DeleteMapping("/delete-lesson{lessonId}")
    public ResponseEntity<String> deleteLesson(@PathVariable UUID lessonId){
        return ResponseEntity.status(200).body(lessonService.delete(lessonId));
    }

    @GetMapping("/get-all-lesson")
    public List<LessonResponse> getAllLesson(){
        return lessonService.getAll();
    }

    @GetMapping("get-lesson/{id}")
    public LessonEntity getLessonById(@PathVariable UUID id) {
        return lessonService.getLesson(id);
    }

    @PutMapping("/update-lesson{lessonId}")
    public ResponseEntity<String> updateLesson(@PathVariable UUID lessonId,
                                               @RequestBody LessonUpdateDTO updateDTO) {
        return ResponseEntity.status(200).body(lessonService.updateLesson(lessonId, updateDTO));
    }

    // MESSAGE
    @GetMapping("/get-messages{chatId}") // hammada bo'ladi bu API student, admin ham o'zini message larini olishi mumkun
    public List<MessageResponse> getMessages(@PathVariable UUID chatId){
        return messageService.getMessages(chatId);
    }

    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(MessageCreateRequest messageCreateRequest){
        return ResponseEntity.ok(messageService.sendMessage(messageCreateRequest));
    }

    @GetMapping("/get-message-by-chatId")
    private List<MessageResponse> getMessagesByChatId(UUID chatId){
        return messageService.getMessagesByChatId(chatId);
    }

    // MODULE

    @PostMapping("/create-module")
    public ResponseEntity<ModuleResponse> createModule(@RequestBody ModuleCreateRequest createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.create(createRequest));
    }

    @DeleteMapping("/delete-module{moduleId}")
    public ResponseEntity<String> deleteModule(@PathVariable UUID moduleId){
        return ResponseEntity.status(200).body(moduleService.delete(moduleId));
    }

    @GetMapping("/get-all-module")
    public List<ModuleResponse> getAllModule(){
        return moduleService.getAll();
    }

    @GetMapping("get-module/{id}")
    public ModuleEntity getModuleById(@PathVariable UUID id) {
        return moduleService.getModule(id);
    }

    // NOTIFICATION
    @PostMapping("/create-notification")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest notificationRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(notificationRequest));
    }

    @GetMapping("/get-all-notification")
    public List<NotificationEntity> getAllNotification(){
        return notificationService.getAll();
    }

    @GetMapping("get-notification{id}")
    public NotificationResponse getNotificationById(@PathVariable UUID id){
        return notificationService.getById(id);
    }

    @DeleteMapping("delete-notification{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id){
        return ResponseEntity.status(200).body(notificationService.delete(id));
    }

    @GetMapping("/get-notifications-by-student{studentId}")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@PathVariable UUID studentId){
        return ResponseEntity.status(200).body(notificationService.getMyNotifications(studentId));
    }

    // SUBJECT

    @PostMapping("/create-subject")
    public ResponseEntity<SubjectResponse> createSubject(@RequestBody SubjectCreateRequest createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(createRequest));
    }

    @DeleteMapping("/delete-subject{subjectId}")
    public ResponseEntity<String> deleteSubject(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(subjectService.delete(subjectId));
    }

    @GetMapping("/get-all-subject")
    public List<SubjectResponse> getAllSubject(){
        return subjectService.getAll();
    }

    @GetMapping("get-subject/{id}")
    public SubjectEntity getSubjectById(@PathVariable UUID id) {
        return subjectService.getSubject(id);
    }

    // USER
    @GetMapping("/block-unblock-user{phoneNumber}")
    public ResponseEntity<String> blockUnblockUser(@PathVariable String phoneNumber, @RequestParam UserStatus status){
        return ResponseEntity.status(200).body(userService.blockUnblockUser(phoneNumber, status));
    }

    @GetMapping("/get-all")
    public List<UserResponse> getAll(){
        return userService.getAll();
    }

    @GetMapping("get-user/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @PostMapping("/sign-in")
    public UserResponse signIn(
            @RequestBody LoginRequest login) {
        return userService.login(login);
    }
}
