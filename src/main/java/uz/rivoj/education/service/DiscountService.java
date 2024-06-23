package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.DiscountRequest;
import uz.rivoj.education.dto.response.DiscountResponse;
import uz.rivoj.education.entity.DiscountEntity;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.DiscountRepository;
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
    public DiscountResponse create(DiscountRequest discountRequest, UUID studentId){
        UserEntity userEntity = userRepository.findById(studentId).orElseThrow(
                () -> new DataNotFoundException("user not found")
        );
        DiscountEntity discountEntity = modelMapper.map(discountRequest, DiscountEntity.class);
//        discountEntity.setStudent(userEntity);
        return modelMapper.map(discountRepository.save(discountEntity), DiscountResponse.class);
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
