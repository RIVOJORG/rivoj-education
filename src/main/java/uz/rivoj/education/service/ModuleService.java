package uz.rivoj.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final StudentInfoRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public String delete(UUID moduleId){
        ModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + moduleId));
        Optional<List<StudentInfo>> studentInfoList = studentRepository.findByCurrentModule_Id(moduleId);
        studentInfoList.ifPresent(studentInfos -> studentInfos.forEach(studentInfo -> {
            studentInfo.setCurrentModule(null);
            studentInfo.setLesson(null);
            studentRepository.save(studentInfo);
        }));
        List<LessonEntity> lessons = module.getLessons();
        for (LessonEntity lesson : lessons) {
            lesson.setModule(null);
            lesson.getAttendances().forEach(attendance -> attendance.setLesson(null));
            lesson.getComments().forEach(comment -> comment.setLesson(null));
        }
        moduleRepository.deleteById(moduleId);
        return "Successfully deleted!";
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
                    responseList.add(lessonResponse);
                }
            }
        });
        if(!responseList.isEmpty()){
            return responseList;
        }
        throw  new DataNotFoundException("There is not any accessible lesson for this user!");
    }


    public CommentResponse convertToCommentResponse(CommentEntity comment) {
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
                .orElseThrow(() -> new DataNotFoundException("Lessons not found in this module => " + moduleId));
        List<LessonResponse> lessonResponseList = new ArrayList<>();
        TeacherInfoResponse teacherInfoResponse = new TeacherInfoResponse();
        lessonEntities.forEach(lessonEntity -> {
            TeacherInfo teacherInfo = teacherInfoRepository.findById(lessonEntity.getTeacherInfo().getId())
                    .orElseThrow(() -> new DataNotFoundException("TeacherInfo not found!"));
            UserEntity teacher = userRepository.findById(teacherInfo.getTeacher().getId())
                    .orElseThrow(() -> new  DataNotFoundException("Teacher not found!"));
            teacherInfoResponse.setName(teacher.getName());
            teacherInfoResponse.setSurname(teacher.getSurname());
            teacherInfoResponse.setAvatar(teacher.getAvatar());
            teacherInfoResponse.setAbout(teacherInfo.getAbout());
            teacherInfoResponse.setSubject(teacherInfo.getSubject().getTitle());
            LessonResponse lessonResponse = modelMapper.map(lessonEntity, LessonResponse.class);
            lessonResponse.setTeacherInfo(teacherInfoResponse);
            lessonResponse.setAdditionalLinks(lessonEntity.getAdditionalLinks());
            lessonResponseList.add(lessonResponse);
        });
        return lessonResponseList;
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

    public List<ModuleResponse> getAllModulesOfSubject(UUID subjectId) {
        SubjectEntity subjectEntity = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id => " + subjectId));
        List<ModuleEntity> modulesBySubject = moduleRepository.findAllBySubject_IdOrderByNumberAsc(subjectEntity.getId())
                .orElseThrow(() -> new DataNotFoundException("Module not found with this subject => " + subjectEntity.getTitle()));
        return getModuleResponses(modulesBySubject);
    }

    public Object addModule(UUID subjectId,Integer moduleNumber) {
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id => " + subjectId));
        ModuleEntity module = new ModuleEntity(subject,moduleNumber);
        moduleRepository.save(module);
        return "Successfully created";
    }

    public String changeModuleNumber(UUID moduleId, Integer moduleNumber) {
        ModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id => " + moduleId));
        module.setNumber(moduleNumber);
        moduleRepository.save(module);
        return "Successfully changed";
    }
}
