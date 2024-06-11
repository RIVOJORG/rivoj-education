package uz.rivoj.education.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.DiscountRequest;
import uz.rivoj.education.dto.response.*;
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
    @GetMapping("get-progress/{phoneNumber}")
    public HomePageResponse getProgressByPhoneNumber(@PathVariable String phoneNumber) {
        return progressService.getProgressByPhoneNumber(phoneNumber);
    }

    @GetMapping("get-lesson/{studentId}{lessonId}")
    public LessonPageResponse getLessonPageResponseByLessonId(@PathVariable UUID studentId, @PathVariable UUID lessonId) {
        return progressService.getLessonPageResponseByLessonId(studentId, lessonId);
    }

    @GetMapping("/get-ranking-page")
    public RankingPageResponse getRankingPage(){
        return progressService.getRankingPage();
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

    @GetMapping("/get-education{studentId}")
    public EducationPageResponse getEducationPage(@PathVariable UUID studentId){
        return progressService.getEducationPage(studentId);
    }

}

