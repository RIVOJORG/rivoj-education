package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.rivoj.education.repository.ModuleRepository;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
}
