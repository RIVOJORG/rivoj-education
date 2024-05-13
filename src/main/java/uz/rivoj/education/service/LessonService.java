package uz.rivoj.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.LessonCreateRequest;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.dto.update.LessonUpdateDTO;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.exception.DataAlreadyExistsException;
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
        ModuleEntity moduleEntity = moduleRepository.findById(createRequest.getModuleId())
                .orElseThrow(() -> new EntityNotFoundException("Module not found with this id " + createRequest.getModuleId()));
        if (lessonRepository.existsByNumber(createRequest.getNumber())){
            throw new DataAlreadyExistsException("Lesson already exists with number : " + createRequest.getNumber());
        }
        if (lessonRepository.existsByTitle(createRequest.getTitle())){
            throw new DataAlreadyExistsException("Lesson already exists with title : " + createRequest.getTitle());
        }
        LessonEntity lesson = modelMapper.map(createRequest, LessonEntity.class);
        lesson.setModule(moduleEntity);
        lessonRepository.save(lesson);
        LessonResponse lessonResponse = modelMapper.map(createRequest, LessonResponse.class);
        lessonResponse.setId(lesson.getId());
        return lessonResponse;
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

    public String updateLesson(UUID lessonId, LessonUpdateDTO updateDTO) {
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with this id: " + lessonId));

        if (updateDTO.getModuleId() != null) {
            ModuleEntity moduleEntity = moduleRepository.findById(updateDTO.getModuleId())
                    .orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + updateDTO.getModuleId()));
            lesson.setModule(moduleEntity);}
        if (updateDTO.getNumber() != null) {
            lesson.setNumber(updateDTO.getNumber());}
        if (updateDTO.getTitle() != null) {
            lesson.setTitle(updateDTO.getTitle());}
        if (updateDTO.getSource() != null) {
            lesson.setSource(updateDTO.getSource());}
         lessonRepository.save(lesson);
        return "Successfully updated";
    }

    public List<LessonResponse> getLessonsByModule(int page, int size, UUID moduleId) {
        Pageable pageable = PageRequest.of(page, size);
        ModuleEntity moduleEntity = moduleRepository.findById(moduleId).orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + moduleId));
        List<LessonEntity> lessonEntityList = lessonRepository.findLessonsByModule(pageable, moduleEntity).getContent();
        return modelMapper.map(lessonEntityList, new TypeToken<List<LessonResponse>>(){}.getType());
    }

    public LessonEntity findFirstLessonOfNextModule(ModuleEntity module) {
        Integer nextModuleNumber = module.getNumber() + 1;
        ModuleEntity nextModule = moduleRepository.findBySubjectAndNumber(module.getSubject(), nextModuleNumber);

        if (nextModule != null) {
            return lessonRepository.findFirstByModuleOrderByNumberAsc(nextModule);
        }

        return null;
    }

}
