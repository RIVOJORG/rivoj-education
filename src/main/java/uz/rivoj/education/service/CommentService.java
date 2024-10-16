package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.CommentCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Lazy
public class CommentService {
    private final CommentRepository commentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final  ModuleService moduleService;

    public CommentResponse create(CommentCR createRequest, UUID ownerId) {
        LessonEntity lessonEntity = lessonRepository.findById(createRequest.getLessonId())
                .orElseThrow(() -> new ClassCastException("Comment not found with ID: " + createRequest.getLessonId()));
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataNotFoundException("user not found with this id: " + ownerId));
        StudentInfo studentInfo = studentInfoRepository.findByStudentId(ownerId)
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id: " + ownerId));
        CommentEntity comment = new CommentEntity();
        comment.setLesson(lessonEntity);
        comment.setCreatedDate(LocalDateTime.now());
        comment.setDescription(createRequest.getDescription());
        comment.setOwner(owner);
        commentRepository.save(comment);
        return CommentResponse.builder()
                .commentId(comment.getId())
                .avatar(studentInfo.getAvatar())
                .description(comment.getDescription())
                .lessonId(lessonEntity.getId())
                .name(owner.getName())
                .surname(owner.getSurname())
                .ownerId(owner.getId())
                .build();

    }

    public String delete(UUID commentId){
        commentRepository.deleteById(commentId);
        return "Successfully deleted";
    }



    public LessonResponse getLessonWithComments(UUID lessonId) {
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found"));
        Optional<List<CommentEntity>> comments = commentRepository.findByLessonId(lessonId);
        List<CommentResponse> commentResponses = new ArrayList<>();
        if(comments.isPresent()){
            List<CommentEntity> commentEntities = comments.get();
            commentResponses = commentEntities.stream()
                    .map(comment -> {
                        UserEntity owner = comment.getOwner();
                        return CommentResponse.builder()
                                .commentId(comment.getId())
                                .ownerId(owner.getId())
                                .lessonId(comment.getLesson().getId())
                                .name(owner.getName())
                                .surname(owner.getSurname())
                                .avatar(owner.getAvatar())
                                .description(comment.getDescription())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return LessonResponse.builder()
                        .id(lesson.getId())
                        .number(lesson.getNumber())
                        .title(lesson.getTitle())
                        .source(lesson.getSource())
                        .cover(lesson.getCover())
                        .description(lesson.getDescription())
                        .build();
    }
    public Map<String, Object> getCommentsOfLesson(UUID lessonId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber -1, pageSize);
        Page<CommentEntity> commentEntityPage = commentRepository.findByLessonId(lessonId,pageable);
        List<CommentResponse> commentResponseList = commentEntityPage.get().map(commentEntity -> moduleService.convertToCommentResponse((CommentEntity) commentEntity)).collect(Collectors.toList());

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("pageNumber", commentEntityPage.getNumber() + 1);
        responseMap.put("totalPages", commentEntityPage.getTotalPages());
        responseMap.put("totalCount", commentEntityPage.getTotalElements());
        responseMap.put("pageSize", commentEntityPage.getSize());
        responseMap.put("hasPreviousPage", commentEntityPage.hasPrevious());
        responseMap.put("hasNextPage", commentEntityPage.hasNext());
        responseMap.put("data", commentResponseList);
        return responseMap;

    }


    public void editComment(UUID ownerId, UUID commentId,String text) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found with this id: " + commentId));
        if(comment.getOwner().getId().equals(ownerId)){
            comment.setDescription(text);
            commentRepository.save(comment);
        }
    }

    public void deleteComment(UUID ownerId, UUID commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found with this id: " + commentId));
        if(comment.getOwner().getId().equals(ownerId)){
            commentRepository.delete(comment);
        }
    }
}
