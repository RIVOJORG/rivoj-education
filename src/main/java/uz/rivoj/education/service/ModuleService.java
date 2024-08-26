package uz.rivoj.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.ModuleCR;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.dto.response.ModuleResponse;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.SubjectEntity;
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
    private final UserRepository userRepository;
    private final StudentInfoRepository studentRepository;
    private final LessonRepository lessonRepository;

    public ModuleResponse create(ModuleCR createRequest) {
        SubjectEntity subjectEntity = subjectRepository.findById(createRequest.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with this id " + createRequest.getSubjectId()));

        ModuleEntity module = modelMapper.map(createRequest, ModuleEntity.class);
        module.setSubject(subjectEntity);
        moduleRepository.save(module);
        return modelMapper.map(createRequest, ModuleResponse.class);
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


    public List<ModuleResponse> getAll() {
        List<ModuleResponse> list = new ArrayList<>();
        for (ModuleEntity module : moduleRepository.findAll()) {
            ModuleResponse moduleResponse = modelMapper.map(module, ModuleResponse.class);
            moduleResponse.setSubject(module.getSubject().getTitle());
            list.add(moduleResponse);
        }
        return list;
    }

    public ModuleEntity findFirstModuleOfSubject(SubjectEntity subject) {
        return moduleRepository.findFirstBySubjectOrderByNumber(subject);
    }

    public ModuleResponse findByModuleId(UUID moduleId) {
        ModuleEntity moduleEntity = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + moduleId));
        return ModuleResponse.builder()
                .modulNumber(moduleEntity.getNumber())
                .subject(moduleEntity.getSubject().getTitle())
                .build();
    }

    public List<ModuleResponse> getAllModules(UUID userId) {
        StudentInfo  studentInfo= studentRepository.findStudentInfoByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id => " + userId));
        List<ModuleEntity> modulesBySubject = moduleRepository.findAllBySubject(studentInfo.getSubject())
                .orElseThrow(() -> new DataNotFoundException("Module not found with this subject => " + studentInfo.getSubject().getTitle()));
        List<ModuleResponse> modules = new ArrayList<>();
        modulesBySubject.forEach(module -> {
            ModuleResponse moduleResponse = new ModuleResponse();
            moduleResponse.setModule_id(module.getId());
            moduleResponse.setModulNumber(module.getNumber());
            moduleResponse.setSubject(module.getSubject().getTitle());
            modules.add(moduleResponse);
        });
        return modules;
    }

    public List<LessonResponse> getAllAccessibleLessonsOfUser(UUID userId, UUID moduleId) {
        StudentInfo studentInfo = studentRepository.findStudentInfoByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id => " + userId));
        List<ModuleResponse> allModules = getAllModules(userId);
        List<LessonResponse> responseList = new ArrayList<>();
        int currentLesson = studentInfo.getLesson().getNumber();
        System.out.println("Current lesson: " + currentLesson);
        System.out.println("Is empty " + allModules.isEmpty());
        allModules.forEach(module -> {
            if (Objects.equals(module.getModule_id(),moduleId)) {
                List<LessonResponse> lessonResponseList = getAllLessonsByModule(moduleId);
                for (LessonResponse lessonResponse : lessonResponseList) {
                    System.out.println("LessonNumber " + lessonResponse.getNumber());
                    if (currentLesson >= lessonResponse.getNumber()){
                        responseList.add(lessonResponse);
                    }else {
                        lessonResponse.setSource(null);
                        responseList.add(lessonResponse);
                    }
                }
            }
        });
        if(!responseList.isEmpty()){
            return responseList;
        }
        throw  new DataNotFoundException("There is not any accessible lesson for this user!");
    }

    private List<LessonResponse> getAllLessonsByModule(UUID moduleId) {
        ModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id => " + moduleId));
        List<LessonEntity> lessonEntities = lessonRepository.findAllByModule(module)
                .orElseThrow(() -> new DataNotFoundException("Lesson not found in this module => " + moduleId));
        return lessonEntities.stream()
                .map(lessonEntity -> modelMapper.map(lessonEntity, LessonResponse.class))
                .collect(Collectors.toList());
    }
}
