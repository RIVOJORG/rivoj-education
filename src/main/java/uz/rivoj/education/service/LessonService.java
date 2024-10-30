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
import uz.rivoj.education.dto.response.*;
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
    private final CommentRepository commentRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentInfoRepository studentInfoRepository;

    @SneakyThrows
    public LessonResponse create(LessonCR createRequest)  {
        ModuleEntity moduleEntity = moduleRepository.findById(createRequest.getModuleId())
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id " + createRequest.getModuleId()));
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(createRequest.getTeacherId())
                .orElseThrow(() -> new DataNotFoundException("Teacher not found with this id " + createRequest.getTeacherId()));
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
        lesson.setAttendances(new ArrayList<>());
        lesson.setComments(new ArrayList<>());
        lesson.setTeacherInfo(teacherInfo);
        LessonEntity savedLesson = lessonRepository.save(lesson);
        String source = uploadService.uploadFile(createRequest.getLessonVideo(),"Lesson"+savedLesson.getNumber()+"Content");
        String cover = uploadService.uploadFile(createRequest.getCoverOfLesson(),"CoverOfLesson"+savedLesson.getNumber());
        savedLesson.setSource(source);
        savedLesson.setCover(cover);
        lessonRepository.save(savedLesson);
        LessonResponse lessonResponse = modelMapper.map(savedLesson, LessonResponse.class);
        lessonResponse.setTeacherInfo(modelMapper.map(savedLesson.getTeacherInfo(), TeacherInfoResponse.class));
        return lessonResponse;
    }


    @Transactional
    public String delete(UUID lessonId) {
        LessonEntity lessonToDelete = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with id: " + lessonId));

        commentRepository.deleteAllByLessonId(lessonId);

        List<AttendanceEntity> attendanceEntities = attendanceRepository.findAllByLesson_Id(lessonId).orElse(new ArrayList<>());
        attendanceEntities.forEach(attendanceEntity -> {
            attendanceEntity.getAnswers().forEach(uploadService::deleteFile);
            attendanceRepository.delete(attendanceEntity);
        });

        ModuleEntity currentModule = lessonToDelete.getModule();
        Integer lessonNumber = lessonToDelete.getNumber();

        Optional<LessonEntity> previousLesson = lessonRepository.findTopByModuleAndNumberLessThanOrderByNumberDesc(currentModule, lessonNumber);

        if (previousLesson.isPresent()) {
            List<StudentInfo> studentInfos = studentInfoRepository.findByLesson_Id(lessonId).orElse(new ArrayList<>());
            studentInfos.forEach(studentInfo -> {
                studentInfo.setLesson(previousLesson.get());
                studentInfoRepository.save(studentInfo);
            });
        } else {
            Optional<ModuleEntity> previousModule = moduleRepository.findTopBySubjectAndNumberLessThanOrderByNumberDesc(
                    currentModule.getSubject(), currentModule.getNumber());

            if (previousModule.isPresent()) {
                Optional<LessonEntity> lastLessonInPreviousModule = lessonRepository.findTopByModuleIdOrderByNumberDesc(previousModule.get().getId());
                lastLessonInPreviousModule.ifPresent(lessonEntity -> {
                    List<StudentInfo> studentInfos = studentInfoRepository.findByLesson_Id(lessonId).orElse(new ArrayList<>());
                    studentInfos.forEach(studentInfo -> {
                        studentInfo.setLesson(lessonEntity);
                        studentInfoRepository.save(studentInfo);
                    });
                });
            } else {
                List<StudentInfo> studentInfos = studentInfoRepository.findByLesson_Id(lessonId).orElse(new ArrayList<>());
                studentInfos.forEach(studentInfo -> {
                    studentInfo.setLesson(null);
                    studentInfoRepository.save(studentInfo);
                });
            }
        }

        lessonRepository.delete(lessonToDelete);

        return "Deleted!";
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
    public String updateLesson(LessonUpdateDTO updateDTO) {
        LessonEntity lesson = lessonRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new DataNotFoundException("Lesson not found with this id: " + updateDTO.getId()));
        if(updateDTO.getLessonVideo() != null){
            String fileName = "Lesson"+lesson.getNumber()+"Content";
            lesson.setSource(uploadService.uploadFile(updateDTO.getLessonVideo(),fileName));
        }
        if(updateDTO.getCoverOfLesson() != null){
            String fileName = "coverOfLesson"+lesson.getNumber();
            lesson.setCover(uploadService.uploadFile(updateDTO.getCoverOfLesson(),fileName));
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
        if(updateDTO.getTeacherId() != null){
            lesson.setTeacherInfo(teacherInfoRepository.findById(updateDTO.getTeacherId()).get());
        }
        lessonRepository.save(lesson);
        return "Successfully updated!";

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
            response.setTeacherId(lessonEntity.getTeacherInfo().getId());
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
            response.setTeacherInfo(new TeacherInfoResponse(teacher.getId(),teacher.getName(),teacher.getSurname(),teacher.getAvatar(),teacherInfo.getSubject().getTitle(),teacherInfo.getAbout()));
        }
        return  response;
    }
}
