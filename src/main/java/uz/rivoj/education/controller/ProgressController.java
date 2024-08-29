package uz.rivoj.education.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.DiscountCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.service.DiscountService;
import uz.rivoj.education.service.ProgressService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/progress")
public class ProgressController {
    private final ProgressService progressService;
    private final DiscountService discountService;
    @GetMapping("get-progress/{phoneNumber}")
    public HomePageResponse getProgressByPhoneNumber(@PathVariable String phoneNumber) {
        return progressService.getProgressByPhoneNumber(phoneNumber);
    }

    @GetMapping("get-lesson/{lessonId}")
    public LessonPageResponse getLessonPageResponseByLessonId(Principal principal, @PathVariable UUID lessonId) {
        return progressService.getLessonPageResponseByLessonId(principal.getName(), lessonId);
    }

    @GetMapping("/get-ranking-page")
    public RankingPageResponse getRankingPage(){
        return progressService.getRankingPage();
    }

    @PostMapping("/create-discount") // chegirma olish
    public DiscountResponse createDiscount(@RequestBody DiscountCR discountCR, Principal principal){
        return discountService.create(discountCR, UUID.fromString(principal.getName()));
    }

    @GetMapping("/get-discounts-by-student")
    public List<DiscountResponse> getDiscountsByStudentId(Principal principal){
        return discountService.getDiscountsByStudentId(UUID.fromString(principal.getName()));
    }

    @DeleteMapping("/delete-discount{discountId}")
    public String deleteDiscount(@PathVariable UUID discountId){
        return discountService.delete(discountId);
    }

    @GetMapping("/get-education")
    public EducationPageResponse getEducationPage(Principal principal){
        return progressService.getEducationPage(UUID.fromString(principal.getName()));
    }

}

