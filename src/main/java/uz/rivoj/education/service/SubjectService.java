package uz.rivoj.education.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.TeacherInfo;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.dto.request.SubjectCR;
import uz.rivoj.education.dto.response.SubjectResponse;
import uz.rivoj.education.entity.SubjectEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.ModuleRepository;
import uz.rivoj.education.repository.StudentInfoRepository;
import uz.rivoj.education.repository.SubjectRepository;
import uz.rivoj.education.repository.TeacherInfoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final ModuleRepository moduleRepository;
    private final ModelMapper modelMapper;
    private  final StudentInfoRepository studentInfoRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final UploadService uploadService;


    public SubjectResponse create(SubjectCR createRequest) {
        if (subjectRepository.existsByTitle(createRequest.getTitle()))
            throw new DataAlreadyExistsException("Subject already exists with this title: " + createRequest.getTitle());
        SubjectEntity subjectEntity = modelMapper.map(createRequest, SubjectEntity.class);
        subjectRepository.save(subjectEntity);
        for (int i = 1; i <= createRequest.getModuleCount(); i++) {
            ModuleEntity moduleEntity = new ModuleEntity();
            moduleEntity.setSubject(subjectEntity);
            moduleEntity.setNumber(i);
            moduleRepository.save(moduleEntity);
        }
        SubjectResponse subjectResponse = modelMapper.map(createRequest, SubjectResponse.class);
        subjectResponse.setId(subjectEntity.getId());
        return subjectResponse;
    }

    public String delete(UUID subjectId){
        Optional<List<StudentInfo>> studentInfoList = studentInfoRepository.findBySubject_Id(subjectId);
        studentInfoList.ifPresent(studentInfos -> studentInfos.forEach(studentInfo -> {
            studentInfo.setSubject(null);
            studentInfo.setCurrentModule(null);
            studentInfo.setLesson(null);
            studentInfoRepository.save(studentInfo);
        }));
        Optional<List<TeacherInfo>> teacherInfoList = teacherInfoRepository.findBySubject_Id(subjectId);
        teacherInfoList.ifPresent(teacherInfos -> teacherInfos.forEach(teacherInfo -> {
            teacherInfo.setSubject(null);
            teacherInfoRepository.save(teacherInfo);
        }));
        subjectRepository.deleteById(subjectId);
        return "Successfully deleted!" ;
    }

    public SubjectEntity getSubject(UUID subjectId){
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id: " + subjectId));
    }

    @Transactional
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

    public String changeSubjectTitle(UUID subjectId, String subjectName) {
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new DataNotFoundException("Subject not found with this id: " + subjectId));
        subject.setTitle(subjectName);
        subjectRepository.save(subject);
        return "Successfully changed!";
    }
}
