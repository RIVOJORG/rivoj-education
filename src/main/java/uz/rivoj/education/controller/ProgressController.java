package uz.rivoj.education.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.response.RankingPageResponse;
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

    @GetMapping("/get-top10")
    public RankingPageResponse getRankingPage(){
        return progressService.getTop10Students();
    }
    @GetMapping("/get-top10-by-subject")
    public RankingPageResponse getTop10StudentBySubject(Principal principal){
        return progressService.getTop10StudentsBySubject(UUID.fromString(principal.getName()));
    }


    @PostMapping("/create-discount")
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


}

