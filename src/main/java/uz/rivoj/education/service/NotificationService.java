package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.NotificationRequest;
import uz.rivoj.education.dto.response.NotificationResponse;
import uz.rivoj.education.entity.NotificationEntity;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.NotificationRepository;
import uz.rivoj.education.repository.StudentInfoRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public NotificationResponse create(NotificationRequest notificationRequest){
        UserEntity user = userRepository.findById(notificationRequest.getUserId()).orElseThrow(
                () -> new DataNotFoundException("User not found"));
        NotificationEntity notificationEntity = modelMapper.map(notificationRequest, NotificationEntity.class);
        notificationEntity.setUser(user);
        return modelMapper.map(notificationRepository.save(notificationEntity), NotificationResponse.class);
    }

    public String delete(UUID notificationId){
        NotificationEntity notificationEntity = notificationRepository.findById(notificationId).orElseThrow(
                () -> new DataNotFoundException("notification not found")
        );

        notificationRepository.delete(notificationEntity);
        return "notification deleted";
    }

    public NotificationResponse getById(UUID id){
        NotificationEntity notificationEntity = notificationRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("notification not found")
        );
        return modelMapper.map(notificationEntity, NotificationResponse.class);
    }

    public List<NotificationResponse> getAll(){
        List<NotificationResponse> responseList = new ArrayList<>();
        for (NotificationEntity notificationEntity : notificationRepository.findAll()) {
            responseList.add(modelMapper.map(notificationEntity, NotificationResponse.class));
        }
        return responseList;
    }

    public List<NotificationResponse> getMyNotifications(UUID id){
        List<NotificationResponse> notificationsById = new ArrayList<>();
        for (NotificationEntity e : notificationRepository.findAllByUserId(id)){
            notificationsById.add(modelMapper.map(e, NotificationResponse.class));
        }
        return notificationsById;
    }
}
