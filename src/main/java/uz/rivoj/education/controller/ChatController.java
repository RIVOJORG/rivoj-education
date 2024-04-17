package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.response.ChatResponse;
import uz.rivoj.education.dto.response.CommentResponse;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.entity.CommentEntity;
import uz.rivoj.education.service.ChatService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    @PostMapping("/create_chat")
    public ResponseEntity<UUID> createChat(Principal user,UUID user2){
        return ResponseEntity.ok(chatService.createChat(UUID.fromString(user.getName()),user2));
    }
    @DeleteMapping("/delete_chat")
    public ResponseEntity<String> deleteChat(UUID chatId){
        return ResponseEntity.ok(chatService.deleteChat(chatId));
    }

    @GetMapping("/get-all")
    public List<ChatResponse> getAll(){
        return chatService.getAll();
    }

    @GetMapping("get-chat/{id}")
    public ChatEntity getChatById(@PathVariable UUID id) {
        return chatService.getChat(id);
    }
}
