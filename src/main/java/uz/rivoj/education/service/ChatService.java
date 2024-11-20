package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.response.ChatResponse;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.entity.Message;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.ChatRepository;
import uz.rivoj.education.repository.StudentInfoRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final ModelMapper modelMapper;

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

    public List<ChatResponse> getAll() {
        List<ChatResponse> list = new ArrayList<>();
        for (ChatEntity chatEntity : chatRepository.findAll()) {
            list.add(modelMapper.map(chatEntity, ChatResponse.class));
        }
        return list;
    }

    public ChatEntity getChat(UUID chatId){
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new DataNotFoundException("Chat not found with this id: " + chatId));
    }

    public List<ChatResponse> getMyChats(UUID memberId) {
        UserEntity user = userRepository.findById(memberId)
                .orElseThrow(() -> new DataNotFoundException("User not found with this id: " + memberId));
        StudentInfo studentInfo = studentInfoRepository.findByStudentId(memberId)
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id: " + memberId));
        Optional<List<ChatEntity>> chatEntityList = chatRepository.findByMembersContaining(user);
        if (chatEntityList.isPresent()) {
            List<ChatEntity> chatEntities = chatEntityList.get();
            List<ChatResponse> chatResponseList = new ArrayList<>();
            for (ChatEntity chatEntity : chatEntities) {
                ChatResponse chatResponse = ChatResponse.builder()
                        .chatId(chatEntity.getId())
                        .lastMessage(getLastMessageFromChat(chatEntity.getId()))
                        .name(user.getName())
                        .studentAvatar(studentInfo.getStudent().getAvatar())
                        .surname(user.getSurname())
                        .build();
                chatResponseList.add(chatResponse);
            }
            return chatResponseList;
        }else {
            throw new DataNotFoundException("Chat not found with this id: " + memberId);
        }

    }


    public String getLastMessageFromChat(UUID chatId) {
        ChatEntity chatEntity = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with id: " + chatId));
        Message lastMessage = chatEntity.getLastMessage();
     return lastMessage.getText();
    }

}
