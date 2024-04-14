package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.rivoj.education.entity.Message;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.ChatRepository;
import uz.rivoj.education.repository.MessageRepository;
import uz.rivoj.education.repository.UserRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public String sendMessage(UUID sender,UUID chatId, String text) {
        Message message = new Message();
        message.setSender(userRepository.findById(sender)
                .orElseThrow(() -> new DataNotFoundException("User not found! " + sender)));
        message.setChat(chatRepository.findById(chatId)
                .orElseThrow(() -> new DataNotFoundException("Chat not found! " + chatId)));
        message.setText(text);
        return messageRepository.save(message).getText();
    }

    public String deleteMessage(UUID messageId) {
        messageRepository.deleteById(messageId);
        return "Deleted";
    }
    public String editMessage(UUID messageId, String text) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new DataNotFoundException("Message not found! " + messageId));
        message.setText(text);
        return messageRepository.save(message).getText();
    }
}
