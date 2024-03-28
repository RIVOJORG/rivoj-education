package uz.rivoj.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.LessonCreateRequest;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.LessonRepository;
import uz.rivoj.education.repository.ModuleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final ModelMapper modelMapper;


    public LessonResponse create(LessonCreateRequest createRequest) {
        moduleRepository.findById(createRequest.getModuleId())
                .orElseThrow(() -> new EntityNotFoundException("Module not found with this id " + createRequest.getModuleId()));
        LessonEntity lesson = modelMapper.map(createRequest, LessonEntity.class);
        lessonRepository.save(lesson);
        return modelMapper.map(lesson, LessonResponse.class);
    }

    public String delete(UUID lessonId){
        LessonEntity lesson = getLesson(lessonId);
        lessonRepository.deleteById(lesson.getId());
        return "Successfully deleted: ";
    }

    public LessonEntity getLesson(UUID lessonId){
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with this id: " + lessonId));
    }


    public List<LessonResponse> getAll() {
        List<LessonResponse> list = new ArrayList<>();
        for (LessonEntity lesson : lessonRepository.findAll()) {
            list.add(modelMapper.map(lesson, LessonResponse.class));
        }
        return list;
    }
}
