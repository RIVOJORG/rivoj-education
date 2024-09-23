package uz.rivoj.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.response.CommentResponse;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.dto.response.ModuleResponse;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final StudentInfoRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final CommentService commentService;
    private final TeacherInfoRepository teacherInfoRepository;

    public ModuleResponse create(Integer moduleNumber, UUID teacherId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found"));
        SubjectEntity subjectEntity = subjectRepository.findById(teacherInfo.getSubject().getId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with this id " + teacherInfo.getSubject().getId()));

        ModuleEntity module = new ModuleEntity();
        module.setNumber(moduleNumber);
        module.setSubject(subjectEntity);
        moduleRepository.save(module);
        return ModuleResponse.builder()
                .module_id(module.getId())
                .moduleNumber(module.getNumber())
                .subject(module.getSubject().getTitle()).build();
    }

    public String delete(UUID moduleId){
        ModuleEntity module = getModule(moduleId);
        moduleRepository.deleteById(module.getId());
        return "Successfully deleted: ";
    }

    public ModuleEntity getModule(UUID moduleId){
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + moduleId));
    }


    public ModuleResponse findByModuleId(UUID moduleId) {
        ModuleEntity moduleEntity = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + moduleId));
        return ModuleResponse.builder()
                .moduleNumber(moduleEntity.getNumber())
                .subject(moduleEntity.getSubject().getTitle())
                .build();
    }

    public List<ModuleResponse> getAllModulesOfStudent(UUID userId) {
        StudentInfo studentInfo= studentRepository.findByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id => " + userId));
        List<ModuleEntity> modulesBySubject = moduleRepository.findAllBySubject_IdOrderByNumberAsc(studentInfo.getSubject().getId())
                .orElseThrow(() -> new DataNotFoundException("Module not found with this subject => " + studentInfo.getSubject().getTitle()));
        return getModuleResponses(modulesBySubject);
    }

    public List<LessonResponse> getAllAccessibleLessonsOfUser(UUID userId, UUID moduleId) {
        StudentInfo studentInfo = studentRepository.findByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id => " + userId));
        List<ModuleResponse> allModules = getAllModulesOfStudent(userId);
        List<LessonResponse> responseList = new ArrayList<>();
        int currentLesson = studentInfo.getLesson().getNumber();
        allModules.forEach(module -> {
            if (Objects.equals(module.getModule_id(),moduleId)) {
                List<LessonResponse> lessonResponseList = getAllLessonsByModule(moduleId);
                for (LessonResponse lessonResponse : lessonResponseList) {
                    if (currentLesson < lessonResponse.getNumber()) {
                        lessonResponse.setSource(null);
                    }
                    lessonResponse.setComments(commentService.getLessonWithComments(lessonResponse.getId()).getComments());
                    responseList.add(lessonResponse);
                }
            }
        });
        if(!responseList.isEmpty()){
            return responseList;
        }
        throw  new DataNotFoundException("There is not any accessible lesson for this user!");
    }
    public List<CommentResponse> getCommentsByLessonId(UUID lessonId) {
        List<CommentEntity> comments = commentRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new DataNotFoundException("Comments not found with this id => " + lessonId));
        return comments.stream()
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

    public List<LessonResponse> getAllLessonsByModule(UUID moduleId) {
        List<LessonEntity> lessonEntities = lessonRepository.findAllByModule_IdOrderByNumberAsc(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found in this module => " + moduleId));
        List<LessonResponse> lessonResponseList = new ArrayList<>();
        lessonEntities.forEach(lessonEntity -> {
            LessonResponse lessonResponse = modelMapper.map(lessonEntity, LessonResponse.class);
            lessonResponse.setModuleId(lessonEntity.getModule().getId());
            lessonResponseList.add(lessonResponse);
        });
        return lessonResponseList;
    }
    public List<ModuleResponse> getAllModulesOfSubject(UUID subjectId){
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id => " + subjectId));
        List<ModuleEntity> modulesBySubject = moduleRepository.findAllBySubject_IdOrderByNumberAsc(subject.getId())
                .orElseThrow(() -> new DataNotFoundException("Module not found with this subject => " + subject.getTitle()));
        return getModuleResponses(modulesBySubject);
    }

    private List<ModuleResponse> getModuleResponses(List<ModuleEntity> modulesBySubject) {
        List<ModuleResponse> modules = new ArrayList<>();
        modulesBySubject.forEach(module -> {
            ModuleResponse moduleResponse = new ModuleResponse();
            moduleResponse.setModule_id(module.getId());
            moduleResponse.setModuleNumber(module.getNumber());
            moduleResponse.setSubject(module.getSubject().getTitle());
            modules.add(moduleResponse);
        });
        return modules;
    }
}
