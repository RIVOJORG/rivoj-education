package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.dto.request.SubjectCreateRequest;
import uz.rivoj.education.dto.response.SubjectResponse;
import uz.rivoj.education.entity.Subject;
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
            throw new DataAlreadyExistsException("Subject already exists with: " + createRequest.getTitle());
        }
//        if ()
        Subject subject = modelMapper.map(createRequest, Subject.class);
        subjectRepository.save(subject);
        return modelMapper.map(subject, SubjectResponse.class);


    }

    public String delete(UUID subjectId){
        Subject subject = getSubject(subjectId);
        subjectRepository.deleteById(subject.getId());
        return "Successfully deleted: " + subject.getTitle();
    }

    public Subject getSubject(UUID subjectId){
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id: " + subjectId));
    }


    public List<SubjectResponse> getAll() {
        List<SubjectResponse> list = new ArrayList<>();
        for (Subject subject : subjectRepository.findAll()) {
            list.add(modelMapper.map(subject, SubjectResponse.class));
        }
        return list;
    }
}
