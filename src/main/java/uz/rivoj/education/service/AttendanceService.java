package uz.rivoj.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.AttendanceRequest;
import uz.rivoj.education.dto.request.ModuleCreateRequest;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.dto.response.ModuleResponse;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.AttendanceRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

//    public AttendanceResponse create(AttendanceRequest attendanceCr) {
//        Optional<UserEntity> user = userRepository.findById(attendanceCr.getStudentId());
//        attendanceCr.get
//        if(user.isPresent()){
//
//        }
//    }
//
//    public String delete(UUID moduleId){
//        ModuleEntity module = getModule(moduleId);
//        moduleRepository.deleteById(module.getId());
//        return "Successfully deleted: ";
//    }
//
//    public ModuleEntity getModule(UUID moduleId){
//        return moduleRepository.findById(moduleId)
//                .orElseThrow(() -> new DataNotFoundException("Module not found with this id: " + moduleId));
//    }
//
//
//    public List<ModuleResponse> getAll() {
//        List<ModuleResponse> list = new ArrayList<>();
//        for (ModuleEntity module : moduleRepository.findAll()) {
//            list.add(modelMapper.map(module, ModuleResponse.class));
//        }
//        return list;
//    }
}
