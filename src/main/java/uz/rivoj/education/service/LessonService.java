package uz.rivoj.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.LessonCR;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.dto.update.LessonUpdateDTO;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.LessonRepository;
import uz.rivoj.education.repository.ModuleRepository;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final ModelMapper modelMapper;
    private final UploadService uploadService;
    @SneakyThrows
    public LessonResponse create(LessonCR createRequest, MultipartFile lessonVideo, MultipartFile coverOfLesson)  {
        ModuleEntity moduleEntity = moduleRepository.findById(createRequest.getModuleId())
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id " + createRequest.getModuleId()));
        if (lessonRepository.existsByModuleAndNumber(moduleEntity, createRequest.getNumber())) {
            throw new DataAlreadyExistsException("Lesson already exists with number : " + createRequest.getNumber() + " in module id : " + createRequest.getModuleId());
        }
        if (lessonRepository.existsByModuleAndTitle(moduleEntity, createRequest.getTitle())) {
            throw new DataAlreadyExistsException("Lesson already exists with title : " + createRequest.getTitle() + " in module id : " + createRequest.getModuleId());
        }
        LessonEntity lesson = modelMapper.map(createRequest, LessonEntity.class);
        lesson.setModule(moduleEntity);
        LessonEntity savedLesson = lessonRepository.save(lesson);
        String lessonVideoContentType = lessonVideo.getContentType();
        String lessonSuffix = Objects.requireNonNull(lessonVideoContentType).substring(lessonVideoContentType.indexOf("/") + 1);
        String source = uploadService.uploadFile(lessonVideo,"Lesson"+savedLesson.getNumber()+"."+lessonSuffix);
        String coverContentType = coverOfLesson.getContentType();
        String coverSuffix = Objects.requireNonNull(coverContentType).substring(coverContentType.indexOf("/")+1);
        String cover = uploadService.uploadFile(coverOfLesson,"CoverOfLesson"+savedLesson.getNumber()+"."+coverSuffix);
        savedLesson.setSource(source);
        savedLesson.setCover(cover);
        lessonRepository.save(savedLesson);
        return modelMapper.map(savedLesson, LessonResponse.class);
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



    public LessonResponse findByLessonId(UUID lessonId){
        LessonEntity lessonEntity = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with this id: " + lessonId));
        return LessonResponse.builder()
                .cover(lessonEntity.getCover())
                .description(lessonEntity.getDescription())
                .id(lessonEntity.getId())
                .moduleId(lessonEntity.getModule().getId())
                .number(lessonEntity.getNumber())
                .source(lessonEntity.getSource())
                .title(lessonEntity.getTitle())
                .build();
    }
    public List<LessonResponse> getAll() {
        List<LessonResponse> list = new ArrayList<>();
        for (LessonEntity lesson : lessonRepository.findAll()) {
            Optional<ModuleEntity> module = moduleRepository.findById(lesson.getModule().getId());
            LessonResponse response = modelMapper.map(lesson, LessonResponse.class);
            response.setModuleId(module.get().getId());
            response.setNumber(lesson.getNumber());
            list.add(response);
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
        List<LessonResponse> list = new ArrayList<>();
        for (LessonEntity lessonEntity : lessonEntityList) {
            LessonResponse response = modelMapper.map(lessonEntity, LessonResponse.class);
            response.setModuleId(moduleId);
            list.add(response);
        }
        return list;
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
