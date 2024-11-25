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

}

