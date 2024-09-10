package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.CommentCR;
import uz.rivoj.education.dto.request.MessageCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.service.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final MessageService messageService;
    private final ChatService chatService;
    private final NotificationService notificationService;
    private final CommentService commentService;
    private final AttendanceService attendanceService;
    private final UserService userService;
    private final LessonService lessonService;


    @PostMapping("/create_chat")
    public ResponseEntity<UUID> createChat(Principal user, UUID user2){ // start chat
        return ResponseEntity.ok(chatService.createChat(UUID.fromString(user.getName()),user2));
    }

    @GetMapping("/get-my-chats") // hammada bo'ladi bu API student, admin ham o'zini chatlarini olishi mumkun
    public List<ChatResponse> getMyChats(Principal principal){
        return chatService.getMyChats(UUID.fromString(principal.getName()));
    }
//    @GetMapping("/get-messages{chatId}") // hammada bo'ladi bu API student, admin ham o'zini message larini olishi mumkun
//    public List<MessageResponse> getMessages(@PathVariable UUID chatId){
//        return messageService.getMessages(chatId);
//    }

    @GetMapping("get-chat/{id}")
    public ChatEntity getChatById(@PathVariable UUID id) {
        return chatService.getChat(id);
    }



    // MESSAGE
    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(MessageCR messageCreateRequest){
        return ResponseEntity.ok(messageService.sendMessage(messageCreateRequest));
    }

    @GetMapping("/get-message-by-chatId")
    private List<MessageResponse> getMessagesByChatId(UUID chatId){
        return messageService.getMessagesByChatId(chatId);
    }


    @DeleteMapping("/delete-message")
    public ResponseEntity<String > deleteMessage(UUID messageId){
        return ResponseEntity.ok(messageService.deleteMessage(messageId));
    }


    @PutMapping("/edit-message")
    public ResponseEntity<String> editMessage(UUID messageId, String text){
        return ResponseEntity.ok(messageService.editMessage(messageId,text));
    }


    @GetMapping("/get-all-messages")
    public List<MessageResponse> getAll(){
        return messageService.getAllMessages();
    }



    @GetMapping("/get-notifications-by-user")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Principal principal){
        return ResponseEntity.status(200).body(notificationService.getMyNotifications(UUID.fromString(principal.getName())));
    }


    // COMMENT

    @PostMapping("/create-comment")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentCR createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(createRequest));
    }

    // ATTENDANCE
    @GetMapping("/get-attendances-by-lesson{lessonId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendancesByLesson(@PathVariable UUID lessonId){
        return ResponseEntity.ok(attendanceService.getAttendancesByLesson(lessonId));
    }

    @GetMapping("/get-user-details")
    public ResponseEntity<?> getUserDetails(Principal principal){
        return ResponseEntity.ok(userService.getUserDetails(UUID.fromString(principal.getName())));
    }
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonResponse> getLessonWithComments(@PathVariable UUID lessonId) {
        LessonResponse response = commentService.getLessonWithComments(lessonId);
        return ResponseEntity.ok(response);
    }
}
