package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.CommentCR;
import uz.rivoj.education.dto.response.CommentResponse;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.entity.CommentEntity;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.CommentRepository;
import uz.rivoj.education.repository.LessonRepository;
import uz.rivoj.education.repository.StudentInfoRepository;
import uz.rivoj.education.repository.UserRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final ModelMapper modelMapper;

    public CommentResponse create(CommentCR createRequest, UUID ownerId) {
        LessonEntity lessonEntity = lessonRepository.findById(createRequest.getLessonId())
                .orElseThrow(() -> new ClassCastException("Comment not found with ID: " + createRequest.getLessonId()));
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataNotFoundException("user not found with this id: " + ownerId));
        StudentInfo studentInfo = studentInfoRepository.findStudentInfoByStudentId(ownerId)
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
        CommentEntity commentEntity = getComment(commentId);
        commentRepository.deleteById(commentEntity.getId());
        return "Successfully deleted";
    }
    public CommentResponse findByCommentId(UUID commentId){
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found with this id: " + commentId));
        UserEntity user = userRepository.findById(commentEntity.getOwner().getId())
                .orElseThrow(() -> new DataNotFoundException("User not found with this id: " + commentEntity.getOwner().getId()));
        StudentInfo studentInfo = studentInfoRepository.findStudentInfoByStudentId(user.getId())
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id: " + user.getId()));
        return CommentResponse.builder()
                .avatar(studentInfo.getAvatar())
                .commentId(commentEntity.getId())
                .description(commentEntity.getDescription())
                .lessonId(commentEntity.getLesson().getId())
                .name(user.getName())
                .ownerId(user.getId())
                .surname(user.getSurname())
                .build();
    }

    public CommentEntity getComment(UUID commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found with this id: " + commentId));
    }


    public List<CommentResponse> getAll() {
        List<CommentResponse> list = new ArrayList<>();
        for (CommentEntity commentEntity : commentRepository.findAll()) {
            list.add(modelMapper.map(commentEntity, CommentResponse.class));
        }
        return list;
    }

    public LessonResponse getLessonWithComments(UUID lessonId) {
        // Fetch the lesson
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found"));

        // Fetch comments for the lesson
        List<CommentEntity> comments = commentRepository.findByLesson(lesson);

        // Convert comments to CommentResponse
        List<CommentResponse> commentResponses = comments.stream()
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

        // Build the final response
        return LessonResponse.builder()
                        .id(lesson.getId())
                        .number(lesson.getNumber())
                        .title(lesson.getTitle())
                        .source(lesson.getSource())
                        .cover(lesson.getCover())
                        .moduleId(lesson.getModule().getId())
                        .description(lesson.getDescription())
                        .comments(commentResponses)
                .build();
    }

}
