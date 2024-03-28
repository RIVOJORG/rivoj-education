package uz.shaftoli.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shaftoli.education.dto.request.ModuleCreateRequest;
import uz.shaftoli.education.dto.response.ModuleResponse;
import uz.shaftoli.education.entity.Module;
import uz.shaftoli.education.service.ModuleService;

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
    public Module getModuleById(@PathVariable UUID id) {
        return moduleService.getModule(id);
    }
}
