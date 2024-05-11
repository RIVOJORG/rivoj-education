package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.rivoj.education.dto.response.HomePageResponse;
import uz.rivoj.education.dto.response.LessonPageResponse;
import uz.rivoj.education.service.ProgressService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/progress")
public class ProgressController {
    private final ProgressService progressService;
    @GetMapping("get-progress/{phoneNumber}")
    public HomePageResponse getProgressByPhoneNumber(@PathVariable String phoneNumber) {
        return progressService.getProgressByPhoneNumber(phoneNumber);
    }

    @GetMapping("get-lesson/{studentId}{lessonId}")
    public LessonPageResponse getLessonPageResponseByLessonId(@PathVariable UUID studentId, @PathVariable UUID lessonId) {
        return progressService.getLessonPageResponseByLessonId(studentId, lessonId);
    }


}

