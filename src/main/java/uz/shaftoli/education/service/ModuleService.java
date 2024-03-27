package uz.shaftoli.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.shaftoli.education.repository.ModuleRepository;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
}
