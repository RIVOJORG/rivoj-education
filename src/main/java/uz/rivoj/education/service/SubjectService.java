package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.dto.request.SubjectCR;
import uz.rivoj.education.dto.response.SubjectResponse;
import uz.rivoj.education.entity.SubjectEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.ModuleRepository;
import uz.rivoj.education.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final ModuleRepository moduleRepository;
    private final ModelMapper modelMapper;


    public SubjectResponse create(SubjectCR createRequest) {
        if (subjectRepository.existsByTitle(createRequest.getTitle())){
            throw new DataAlreadyExistsException("Subject already exists with this title: " + createRequest.getTitle());}
        SubjectEntity subjectEntity = modelMapper.map(createRequest, SubjectEntity.class);
        subjectRepository.save(subjectEntity);
        for (int i = 1; i <= createRequest.getModuleCount(); i++) {
            ModuleEntity moduleEntity = new ModuleEntity();
            moduleEntity.setSubject(subjectEntity);
            moduleEntity.setModuleNumber(i);
            moduleRepository.save(moduleEntity);
        }
        return modelMapper.map(createRequest, SubjectResponse.class);
    }

    public String delete(UUID subjectId){
        SubjectEntity subjectEntity = getSubject(subjectId);
        subjectRepository.deleteById(subjectEntity.getId());
        return "Successfully deleted: " + subjectEntity.getTitle();
    }

    public SubjectEntity getSubject(UUID subjectId){
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id: " + subjectId));
    }


    public List<SubjectResponse> getAll() {
        List<SubjectResponse> list = new ArrayList<>();
        for (SubjectEntity subjectEntity : subjectRepository.findAll()) {
            list.add(modelMapper.map(subjectEntity, SubjectResponse.class));
        }
        return list;
    }

    public String findBySubjectId(UUID subjectId) {
        SubjectEntity subjectEntity = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id: " + subjectId));
        return subjectEntity.getTitle();

    }
}
