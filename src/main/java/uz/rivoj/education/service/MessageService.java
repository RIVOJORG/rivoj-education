package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.MessageCreateRequest;
import uz.rivoj.education.dto.response.MessageResponse;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.entity.Message;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.ChatRepository;
import uz.rivoj.education.repository.MessageRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;

    public String sendMessage(MessageCreateRequest messageCreateRequest) {
        Message message = new Message();
        message.setSender(userRepository.findById(messageCreateRequest.getSenderId())
                .orElseThrow(() -> new DataNotFoundException("User not found! " + messageCreateRequest.getSenderId())));
        message.setChat(chatRepository.findById(messageCreateRequest.getChatId())
                .orElseThrow(() -> new DataNotFoundException("Chat not found! " + messageCreateRequest.getChatId())));
        message.setText(messageCreateRequest.getText());
        return messageRepository.save(message).getText();
    }

    public String deleteMessage(UUID messageId) {
        messageRepository.deleteById(messageId);
        return "Deleted";
    }
    public String editMessage(UUID messageId, String text) { // update method
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new DataNotFoundException("Message not found! " + messageId));
        message.setText(text);
        return messageRepository.save(message).getText();
    }

    public List<MessageResponse> getAllMessages(){
        List<Message> messageList = messageRepository.findAll();
        List<MessageResponse> messageResponseList = new ArrayList<>();
        for (Message message : messageList) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setChatId(message.getChat().getId());
            messageResponse.setDate(message.getCreatedDate());
            messageResponse.setSenderId(message.getSender().getId());
            messageResponse.setText(message.getText());
        messageResponseList.add(messageResponse);
        }
        return messageResponseList;
    }

    public List<MessageResponse> getMessagesByChatId(UUID chatId) {
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new DataNotFoundException("Chat not found with this id: " + chatId));

        List<Message> messageList = messageRepository.findByChatId(chat.getId());
         return modelMapper.map(messageList, new TypeToken<List<MessageResponse>>(){}.getType());
    }
}
