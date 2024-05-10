package uz.rivoj.education.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.DiscountRequest;
import uz.rivoj.education.dto.response.DiscountResponse;
import uz.rivoj.education.dto.response.HomePageResponse;
import uz.rivoj.education.dto.response.RankingPageResponse;
import uz.rivoj.education.service.DiscountService;
import uz.rivoj.education.service.ProgressService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/progress")
public class ProgressController {
    private final ProgressService progressService;
    private final DiscountService discountService;
    @GetMapping("/get-progress/{phoneNumber}")
    public HomePageResponse getProgressByPhoneNumber(@PathVariable String phoneNumber) {
        return progressService.getProgressByPhoneNumber(phoneNumber);
    }
    /* yana bir tekshirish kerak . Mening yo'nalishim va barcha guruhlar bo'yicha studentlar
         reytingini ha olib kelish kerak ekan. Shularga kerakli veriablerni qoshish kerak 👇 */
    @GetMapping("/get-ranking-page")
    public RankingPageResponse getRanking(){
        return progressService.getRanking();
    }

    @PostMapping("/create-discount{studentId}") // chegirma olish
    public DiscountResponse createDiscount(@RequestBody DiscountRequest discountRequest, @PathVariable UUID studentId){
        return discountService.create(discountRequest, studentId);
    }

    @GetMapping("/get-discounts-by-student{studentId}")
    public List<DiscountResponse> getDiscountsByStudentId(@PathVariable UUID studentId){
        return discountService.getDiscountsByStudentId(studentId);
    }

    @DeleteMapping("/delete-discount{discountId}")
    public String deleteDiscount(@PathVariable UUID discountId){
        return discountService.delete(discountId);
    }
}

