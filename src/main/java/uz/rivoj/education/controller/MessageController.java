package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.service.MessageService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(Principal principal,UUID chatId, String text){
        return ResponseEntity.ok(messageService.sendMessage(UUID.fromString(principal.getName()),chatId,text));
    }

    @DeleteMapping("/delete-message")
    public ResponseEntity<String > deleteMessage(UUID messageId){
        return ResponseEntity.ok(messageService.deleteMessage(messageId));
    }

    @PutMapping("/edit-message")
    public ResponseEntity<String> editMessage(UUID messageId, String text){
        return ResponseEntity.ok(messageService.editMessage(messageId,text));
    }

}
