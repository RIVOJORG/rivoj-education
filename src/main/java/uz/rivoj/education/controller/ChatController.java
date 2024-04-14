package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.rivoj.education.service.ChatService;
import java.security.Principal;
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
}
