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
import org.springframework.cache.annotation.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final StudentInfoRepository studentInfoRepository;

    @CacheEvict(value = "discounts", key = "'studentDiscounts_' + #userId")
    public DiscountResponse create(DiscountCR discountCR, UUID userId){
        UserEntity student = userRepository.findById(UUID.fromString(String.valueOf(userId)))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<StudentInfo> studentInfo = studentInfoRepository.findByStudentId(student.getId());
        if (studentInfo.isEmpty())
            throw new RuntimeException("Student information not found");

        DiscountEntity discountEntity = modelMapper.map(discountCR, DiscountEntity.class);
        discountEntity.setStudent(studentInfo.get());
        discountRepository.save(discountEntity);
        return modelMapper.map(discountEntity, DiscountResponse.class);
    }

    @Cacheable(value = "discounts", key = "'studentDiscounts_' + #userId")
    public List<DiscountResponse> getDiscountsByStudentId(UUID userId){
        UserEntity student = userRepository.findById(UUID.fromString(String.valueOf(userId)))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<StudentInfo> studentInfo = studentInfoRepository.findByStudentId(student.getId());
        if (studentInfo.isEmpty())
            throw new RuntimeException("Student information not found");

        List<DiscountResponse> list = new ArrayList<>();
        Optional<List<DiscountEntity>> discountsOfStudent = discountRepository.findDiscountEntitiesByStudentId(studentInfo.get().getId());
        if (discountsOfStudent.isEmpty())
            throw new RuntimeException("Student discounts not found");

        for (DiscountEntity discount : discountsOfStudent.get()) {
            list.add(modelMapper.map(discount, DiscountResponse.class));
        }
        return list;
    }

    @CacheEvict(value = "discounts", allEntries = true)
    public String delete(UUID discountId){
        DiscountEntity discountEntity = discountRepository.findById(discountId).orElseThrow(
                () -> new DataNotFoundException("discount not found"));
        discountRepository.delete(discountEntity);
        return "notification deleted";
    }
}
