package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.CommentCreateRequest;
import uz.rivoj.education.dto.response.CommentResponse;
import uz.rivoj.education.entity.CommentEntity;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.repository.CommentRepository;
import uz.rivoj.education.repository.LessonRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final LessonRepository lessonRepository;
    private final ModelMapper modelMapper;

    public CommentResponse create(CommentCreateRequest createRequest) {
        LessonEntity lessonEntity = lessonRepository.findById(createRequest.getLessonId())
                .orElseThrow(() -> new ClassCastException("Lesson not found with ID: " + createRequest.getLessonId()));


        CommentEntity commentEntity = modelMapper.map(createRequest, CommentEntity.class);
        commentEntity.setLesson(lessonEntity);
        CommentEntity saved = commentRepository.save(commentEntity);
        return modelMapper.map(saved, CommentResponse.class);

//        LessonEntity lessonEntity = lessonRepository.findById(createRequest.getLessonId())
//                .orElseThrow(() -> new ClassCastException("Lesson not found with ID: " + createRequest.getLessonId()));
//
//        CommentEntity mapped = modelMapper.map(createRequest, CommentEntity.class);
//        mapped.setLesson(lessonEntity);
//
//         commentRepository.save(mapped);
//        return modelMapper.map(createRequest, CommentResponse.class);

    }

}
