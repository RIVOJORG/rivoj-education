package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.response.StudentResponse;
import uz.rivoj.education.repository.StudentInfoRepository;
import uz.rivoj.education.repository.TeacherInfoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherInfoRepository teacherInfoRepository;
    private final StudentInfoRepository studentInfoRepository;

}
