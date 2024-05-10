package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.rivoj.education.dto.response.HomePageResponse;
import uz.rivoj.education.dto.response.RankingPageResponse;
import uz.rivoj.education.service.ProgressService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/progress")
public class ProgressController {
    private final ProgressService progressService;
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
}

