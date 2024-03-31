package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.dto.request.SubjectCreateRequest;
import uz.rivoj.education.dto.response.SubjectResponse;
import uz.rivoj.education.entity.SubjectEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;


    public SubjectResponse create(SubjectCreateRequest createRequest) {
        if (subjectRepository.existsByTitle(createRequest.getTitle())){
            throw new DataAlreadyExistsException("Subject already exists with: " + createRequest.getTitle());}
        SubjectEntity subjectEntity = modelMapper.map(createRequest, SubjectEntity.class);
        subjectRepository.save(subjectEntity);
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
}
