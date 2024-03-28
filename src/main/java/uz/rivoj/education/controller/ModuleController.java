package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.rivoj.education.service.ModuleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subject")
public class ModuleController {
    private final ModuleService moduleService;
}
