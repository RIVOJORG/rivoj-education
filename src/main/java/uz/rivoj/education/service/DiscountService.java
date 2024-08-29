package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.DiscountCR;
import uz.rivoj.education.dto.response.DiscountResponse;
import uz.rivoj.education.entity.DiscountEntity;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.DiscountRepository;
import uz.rivoj.education.repository.StudentInfoRepository;
import uz.rivoj.education.repository.UserRepository;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final StudentInfoRepository studentInfoRepository;
    public DiscountResponse create(DiscountCR discountCR, UUID studentId){
        UserEntity student = userRepository.findById(UUID.fromString(String.valueOf(studentId)))
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentInfo studentInfo = studentInfoRepository.findByStudent(student);
        if (studentInfo == null) {
            throw new RuntimeException("Student information not found");
        }
        DiscountEntity discountEntity = modelMapper.map(discountCR, DiscountEntity.class);
        discountEntity.setStudent(studentInfo);
        discountRepository.save(discountEntity);
        return modelMapper.map(discountEntity, DiscountResponse.class);
    }

    public List<DiscountResponse> getDiscountsByStudentId(UUID studentId){
        List<DiscountResponse> list = new ArrayList<>();
        for (DiscountEntity discount : discountRepository.findDiscountEntitiesByStudentId(studentId)) {
            list.add(modelMapper.map(discount, DiscountResponse.class));
        }
        return list;
    }

    public String delete(UUID discountId){
        DiscountEntity discountEntity = discountRepository.findById(discountId).orElseThrow(
                () -> new DataNotFoundException("discount not found"));
        discountRepository.delete(discountEntity);
        return "notification deleted";
    }
}
