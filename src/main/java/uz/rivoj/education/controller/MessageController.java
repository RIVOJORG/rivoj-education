package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.MessageCreateRequest;
import uz.rivoj.education.entity.Message;
import uz.rivoj.education.service.MessageService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(MessageCreateRequest messageCreateRequest){
        return ResponseEntity.ok(messageService.sendMessage(messageCreateRequest));
    }

    @DeleteMapping("/delete-message")
    public ResponseEntity<String > deleteMessage(UUID messageId){
        return ResponseEntity.ok(messageService.deleteMessage(messageId));
    }

    @PutMapping("/edit-message")
    public ResponseEntity<String> editMessage(UUID messageId, String text){
        return ResponseEntity.ok(messageService.editMessage(messageId,text));
    }

    @GetMapping("/get-all")
    public List<Message> getAll(){
        return messageService.getAll();
    }

}
