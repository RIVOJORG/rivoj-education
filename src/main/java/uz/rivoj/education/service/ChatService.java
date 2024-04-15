package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.ChatRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public UUID createChat(UUID user, UUID user2) {
        List<UserEntity> members = userRepository.findAllById(new ArrayList<>(List.of(user,user2)));
        ChatEntity chat = new ChatEntity();
        chat.setMembers(members);
        return chatRepository.save(chat).getId();
    }

    public String deleteChat(UUID chatId) {
        chatRepository.deleteById(chatId);
        return "Chat deleted";
    }

}
