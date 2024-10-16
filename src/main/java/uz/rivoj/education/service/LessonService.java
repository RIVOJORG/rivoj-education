package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.LessonCR;
import uz.rivoj.education.dto.response.CommentResponse;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.dto.response.SpecialLessonResponse;
import uz.rivoj.education.dto.response.TeacherInfoResponse;
import uz.rivoj.education.dto.update.LessonUpdateDTO;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.util.*;
import java.util.stream.Collectors;
@Lazy
@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final ModelMapper modelMapper;
    private final UploadService uploadService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final UserRepository userRepository;

    @SneakyThrows
    public LessonResponse create(LessonCR createRequest, MultipartFile lessonVideo, MultipartFile coverOfLesson,UUID teacherId)  {
        ModuleEntity moduleEntity = moduleRepository.findById(createRequest.getModuleId())
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id " + createRequest.getModuleId()));
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found with this id " + teacherId));
        if (lessonRepository.existsByModuleAndTitle(moduleEntity, createRequest.getTitle())) {
            throw new DataAlreadyExistsException("Lesson already exists with this title : " + createRequest.getTitle() + " in module id : " + createRequest.getModuleId());
        }
        Optional<List<LessonEntity>> lessonEntities = lessonRepository.findAllByModule_IdOrderByNumberAsc(moduleEntity.getId());
        LessonEntity lesson = modelMapper.map(createRequest, LessonEntity.class);
        if (lessonEntities.isEmpty()) {
            lesson.setNumber(1);
        }else {
            List<LessonEntity> lessonEntityList = lessonEntities.get();
            int maxNumber = lessonEntityList.stream()
                    .mapToInt(LessonEntity::getNumber)
                    .max()
                    .orElse(0);
            lesson.setNumber(maxNumber + 1);
        }

        lesson.setModule(moduleEntity);
        lesson.setTeacherInfo(teacherInfo);
        LessonEntity savedLesson = lessonRepository.save(lesson);
        String source = uploadService.uploadFile(lessonVideo,"Lesson"+savedLesson.getNumber()+"Content");
        String cover = uploadService.uploadFile(coverOfLesson,"CoverOfLesson"+savedLesson.getNumber());
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
                .number(lessonEntity.getNumber())
                .source(lessonEntity.getSource())
                .title(lessonEntity.getTitle())
                .build();
    }



    public List<CommentResponse> getCommentsByLessonId(UUID lessonId) {
        Optional<List<CommentEntity>> comments = commentRepository.findByLessonId(lessonId);
        if (comments.isEmpty()) {
            throw new DataNotFoundException("Comment not found with this id: " + lessonId);
        }
        return comments.get().stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse convertToCommentResponse(CommentEntity comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .ownerId(comment.getOwner().getId())
                .lessonId(comment.getLesson().getId())
                .name(comment.getOwner().getName())
                .surname(comment.getOwner().getSurname())
                .avatar(comment.getOwner().getAvatar())
                .description(comment.getDescription())
                .build();
    }
    public List<LessonResponse> getAll() {
        List<LessonResponse> list = new ArrayList<>();
        for (LessonEntity lesson : lessonRepository.findAll()) {
            Optional<ModuleEntity> module = moduleRepository.findById(lesson.getModule().getId());
            LessonResponse response = modelMapper.map(lesson, LessonResponse.class);
            response.setNumber(lesson.getNumber());
            list.add(response);
        }
        return list;
    }

    @SneakyThrows
    public String updateLesson(LessonUpdateDTO updateDTO, MultipartFile videoFile,MultipartFile cover) {
        LessonEntity lesson = lessonRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with this id: " + updateDTO.getId()));
        if(videoFile != null){
            String fileName = "Lesson"+lesson.getNumber()+"Content";
            lesson.setSource(uploadService.uploadFile(videoFile,fileName));
        }
        if(cover != null){
            String fileName = "coverOfLesson"+lesson.getNumber();
            lesson.setCover(uploadService.uploadFile(cover,fileName));
        }
        if(updateDTO.getTitle() != null){
            lesson.setTitle(updateDTO.getTitle());
        }
        if(updateDTO.getAdditionalLinks() != null){
            lesson.setAdditionalLinks(updateDTO.getAdditionalLinks());
        }
        if(updateDTO.getDescription() != null){
            lesson.setDescription(updateDTO.getDescription());
        }
        lessonRepository.save(lesson);
        return "Successfully updated: ";

    }
    @Transactional
    public List<SpecialLessonResponse> getLessonsByModule(UUID moduleId) {
        ModuleEntity moduleEntity = moduleRepository.findById(moduleId).orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + moduleId));
        Optional<List<LessonEntity>> lessonEntityList = lessonRepository.findAllByModule_IdOrderByNumberAsc(moduleEntity.getId());
        if(lessonEntityList.isEmpty()){
            throw new DataNotFoundException("Lessons not found with this id: " + moduleId);
        }
        List<SpecialLessonResponse> list = new ArrayList<>();
        for (LessonEntity lessonEntity : lessonEntityList.get()) {
            SpecialLessonResponse response = modelMapper.map(lessonEntity, SpecialLessonResponse.class);
            list.add(response);
        }
        return list;
    }

    @Transactional
    public LessonResponse getLessonById(UUID lessonId) {
        LessonEntity lessonEntity = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with this id: " + lessonId));
        LessonResponse response = modelMapper.map(lessonEntity, LessonResponse.class);
        TeacherInfo teacherInfo = lessonEntity.getTeacherInfo();
        if(teacherInfo != null){
            UserEntity teacher = teacherInfo.getTeacher();
            response.setTeacherInfo(new TeacherInfoResponse(teacher.getName(),teacher.getSurname(),teacher.getAvatar(),teacherInfo.getSubject().getTitle(),teacherInfo.getAbout()));
        }

        return  response;


    }
}
