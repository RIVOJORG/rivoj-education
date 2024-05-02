package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.NotificationRequest;
import uz.rivoj.education.dto.response.NotificationResponse;
import uz.rivoj.education.dto.response.UserResponse;
import uz.rivoj.education.entity.NotificationEntity;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.NotificationRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public NotificationResponse create(NotificationRequest notificationRequest){
        NotificationEntity notificationEntity = modelMapper.map(notificationRequest, NotificationEntity.class);
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

    public List<NotificationEntity> getAll(){
        return notificationRepository.findAll();
    }

    public List<NotificationResponse> getMyNotifications(UUID id){
        List<NotificationResponse> notificationsById = new ArrayList<>();
        for (NotificationEntity e : notificationRepository.findAllByStudentId(id)){
            notificationsById.add(modelMapper.map(e, NotificationResponse.class));
        }
        return notificationsById;
    }
}
