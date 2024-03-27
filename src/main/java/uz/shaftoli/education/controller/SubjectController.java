package uz.shaftoli.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.shaftoli.education.dto.request.SubjectCreateRequest;
import uz.shaftoli.education.dto.response.SubjectResponse;
import uz.shaftoli.education.entity.Subject;

import uz.shaftoli.education.service.SubjectService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subject")
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping("/create-subject")
    public ResponseEntity<SubjectResponse> createSubject(@RequestBody SubjectCreateRequest createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(createRequest));
    }

    @DeleteMapping("/delete-subject{subjectId}")
    public ResponseEntity<String> deleteSubject(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(subjectService.delete(subjectId));
    }

    @GetMapping("/get-all")
    public List<SubjectResponse> getAll(){
        return subjectService.getAll();
    }

    @GetMapping("get-subject/{id}")
    public Subject getSubjectById(@PathVariable UUID id) {
        return subjectService.getSubject(id);
    }
}
