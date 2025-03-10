package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.rivoj.education.dto.response.RankingPageResponse;

import java.security.Principal;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mee")
public class MeController {
    @GetMapping
    public String getTest() {
        return "woring";
    }
}
