package uz.shaftoli.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uz.shaftoli.education.dto.request.SubjectCreateRequest;
import uz.shaftoli.education.dto.response.SubjectResponse;
import uz.shaftoli.education.entity.Subject;
import uz.shaftoli.education.exception.DataAlreadyExistsException;
import uz.shaftoli.education.exception.DataNotFoundException;
import uz.shaftoli.education.repository.SubjectRepository;

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
