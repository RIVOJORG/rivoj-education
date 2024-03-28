package uz.shaftoli.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.shaftoli.education.dto.request.LessonCreateRequest;
import uz.shaftoli.education.dto.request.ModuleCreateRequest;
import uz.shaftoli.education.dto.response.LessonResponse;
import uz.shaftoli.education.dto.response.ModuleResponse;
import uz.shaftoli.education.entity.Lesson;
import uz.shaftoli.education.entity.Module;
import uz.shaftoli.education.exception.DataNotFoundException;
import uz.shaftoli.education.repository.LessonRepository;
import uz.shaftoli.education.repository.ModuleRepository;
import uz.shaftoli.education.repository.SubjectRepository;

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
        Lesson lesson = modelMapper.map(createRequest, Lesson.class);
        lessonRepository.save(lesson);
        return modelMapper.map(lesson, LessonResponse.class);
    }

    public String delete(UUID lessonId){
        Lesson lesson = getLesson(lessonId);
        lessonRepository.deleteById(lesson.getId());
        return "Successfully deleted: ";
    }

    public Lesson getLesson(UUID lessonId){
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with this id: " + lessonId));
    }


    public List<LessonResponse> getAll() {
        List<LessonResponse> list = new ArrayList<>();
        for (Lesson lesson : lessonRepository.findAll()) {
            list.add(modelMapper.map(lesson, LessonResponse.class));
        }
        return list;
    }
}
