package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.CommentCreateRequest;
import uz.rivoj.education.dto.response.CommentResponse;
import uz.rivoj.education.entity.CommentEntity;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.CommentRepository;
import uz.rivoj.education.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final LessonRepository lessonRepository;
    private final ModelMapper modelMapper;

    public CommentResponse create(CommentCreateRequest createRequest) {
        LessonEntity lessonEntity = lessonRepository.findById(createRequest.getLessonId())
                .orElseThrow(() -> new ClassCastException("Lesson not found with ID: " + createRequest.getLessonId()));

        CommentEntity mapped = modelMapper.map(createRequest, CommentEntity.class);
        mapped.setLesson(lessonEntity);
        commentRepository.save(mapped);
        return modelMapper.map(createRequest, CommentResponse.class);

    }

    public String delete(UUID commentId){
        CommentEntity commentEntity = getComment(commentId);
        commentRepository.deleteById(commentEntity.getId());
        return "Successfully deleted";
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

}
