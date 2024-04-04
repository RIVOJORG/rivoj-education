package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.ModuleCreateRequest;
import uz.rivoj.education.dto.response.ModuleResponse;
import uz.rivoj.education.entity.ModuleEntity;
import uz.rivoj.education.service.ModuleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/module")
public class ModuleController {
    private final ModuleService moduleService;
    @PostMapping("/create-module")
    public ResponseEntity<ModuleResponse> createModule(@RequestBody ModuleCreateRequest createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.create(createRequest));
    }

    @DeleteMapping("/delete-module{moduleId}")
    public ResponseEntity<String> deleteModule(@PathVariable UUID moduleId){
        return ResponseEntity.status(200).body(moduleService.delete(moduleId));
    }

    @GetMapping("/get-all")
    public List<ModuleResponse> getAll(){
        return moduleService.getAll();
    }

    @GetMapping("get-module/{id}")
    public ModuleEntity getModuleById(@PathVariable UUID id) {
        return moduleService.getModule(id);
    }
}
