package uz.shaftoli.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.shaftoli.education.service.SubjectService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subject")
public class SubjectController {
    private final SubjectService subjectService;
}
