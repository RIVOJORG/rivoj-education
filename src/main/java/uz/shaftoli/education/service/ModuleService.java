package uz.shaftoli.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.shaftoli.education.dto.request.ModuleCreateRequest;
import uz.shaftoli.education.dto.response.ModuleResponse;
import uz.shaftoli.education.entity.Module;
import uz.shaftoli.education.exception.DataAlreadyExistsException;
import uz.shaftoli.education.exception.DataNotFoundException;
import uz.shaftoli.education.repository.ModuleRepository;
import uz.shaftoli.education.repository.SubjectRepository;

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
        subjectRepository.findById(createRequest.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with this id " + createRequest.getSubjectId()));
        Module module = modelMapper.map(createRequest, Module.class);
        moduleRepository.save(module);
        return modelMapper.map(module, ModuleResponse.class);
    }

    public String delete(UUID moduleId){
        Module module = getModule(moduleId);
        moduleRepository.deleteById(module.getId());
        return "Successfully deleted: ";
    }

    public Module getModule(UUID moduleId){
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + moduleId));
    }


    public List<ModuleResponse> getAll() {
        List<ModuleResponse> list = new ArrayList<>();
        for (Module module : moduleRepository.findAll()) {
            list.add(modelMapper.map(module, ModuleResponse.class));
        }
        return list;
    }
}
