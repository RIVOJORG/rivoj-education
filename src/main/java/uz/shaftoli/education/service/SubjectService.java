package uz.shaftoli.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.shaftoli.education.repository.SubjectRepository;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;


}
