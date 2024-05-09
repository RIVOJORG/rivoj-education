package uz.rivoj.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.ModuleCreateRequest;
import uz.rivoj.education.dto.response.ModuleResponse;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.entity.SubjectEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.ModuleRepository;
import uz.rivoj.education.repository.SubjectRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    public ModuleResponse create(ModuleCreateRequest createRequest) {
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
            list.add(modelMapper.map(module, ModuleResponse.class));
        }
        return list;
    }

    public ModuleEntity findFirstModuleOfSubject(SubjectEntity subject) {
        return moduleRepository.findFirstBySubjectOrderByNumberAsc(subject);
    }

}
