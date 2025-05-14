package uz.rivoj.education.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProgressController {
    private final ProgressService progressService;
    private final DiscountService discountService;

    @GetMapping("/get-top10")
    public RankingPageResponse getRankingPage(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        log.info("Getting top 10 students ranking for user: {}", userId);
        RankingPageResponse response = progressService.getTop10Students(userId);
        log.debug("Retrieved top 10 students ranking with {} entries",
                response.getBestStudents() != null ? response.getBestStudents().size() : 0);
        return response;
    }

    @GetMapping("/get-top10-by-subject")
    public RankingPageResponse getTop10StudentBySubject(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        log.info("Getting top 10 students by subject for user: {}", userId);
        RankingPageResponse response = progressService.getTop10StudentsBySubject(userId);
        log.debug("Retrieved top 10 students by subject with {} entries",
                response.getBestStudents() != null ? response.getBestStudents().size() : 0);
        return response;
    }

    @PostMapping("/create-discount")
    public DiscountResponse createDiscount(@RequestBody DiscountCR discountCR, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        log.info("Creating discount for user: {}, percentage: {}, coin: {}", userId,
                discountCR.getPercentage(), discountCR.getCoin());
        try {
            DiscountResponse response = discountService.create(discountCR, userId);
            log.info("Discount created successfully with percentage: {}, coin: {}",
                    response.getPercentage(), response.getCoin());
            return response;
        } catch (Exception e) {
            log.error("Failed to create discount for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/get-discounts-by-student")
    public List<DiscountResponse> getDiscountsByStudentId(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        log.info("Getting discounts for student: {}", userId);
        List<DiscountResponse> discounts = discountService.getDiscountsByStudentId(userId);
        log.debug("Retrieved {} discounts for student {}", discounts.size(), userId);
        return discounts;
    }

    @DeleteMapping("/delete-discount{discountId}")
    public String deleteDiscount(@PathVariable UUID discountId) {
        log.info("Deleting discount with ID: {}", discountId);
        try {
            String result = discountService.delete(discountId);
            log.info("Discount deleted successfully: {}", discountId);
            return result;
        } catch (Exception e) {
            log.error("Failed to delete discount {}: {}", discountId, e.getMessage());
            throw e;
        }
    }
}
