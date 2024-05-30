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

    @PostMapping("/create")
    public ResponseEntity<AttendanceResponse> createAttendance(@RequestBody AttendanceRequest attendance){
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(attendance));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.getAttendance(id));
    }

    // CHAT
    @PostMapping("/create_chat")
    public ResponseEntity<UUID> createChat(Principal user, UUID user2){ // start chat
        return ResponseEntity.ok(chatService.createChat(UUID.fromString(user.getName()),user2));
    }
    @GetMapping("/get-my-chats{memberId}") // hammada bo'ladi bu API student, admin ham o'zini chatlarini olishi mumkun
    public List<ChatResponse> getMyChats(@PathVariable UUID memberId){
        return chatService.getMyChats(memberId);
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

    @GetMapping("get-module/{id}")
    public ModuleEntity getModuleById(@PathVariable UUID id) {
        return moduleService.getModule(id);
    }

    // NOTIFICATION
    @GetMapping("/get-notifications-by-student{studentId}")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@PathVariable UUID studentId){
        return ResponseEntity.status(200).body(notificationService.getMyNotifications(studentId));
    }

}
