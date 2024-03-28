package uz.shaftoli.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shaftoli.education.dto.request.LessonCreateRequest;
import uz.shaftoli.education.dto.response.LessonResponse;
import uz.shaftoli.education.entity.Lesson;
import uz.shaftoli.education.service.LessonService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/lesson")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/create-lesson")
    public ResponseEntity<LessonResponse> createLesson(@RequestBody LessonCreateRequest createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(createRequest));
    }

    @DeleteMapping("/delete-lesson{lessonId}")
    public ResponseEntity<String> deleteLesson(@PathVariable UUID lessonId){
        return ResponseEntity.status(200).body(lessonService.delete(lessonId));
    }

    @GetMapping("/get-all")
    public List<LessonResponse> getAll(){
        return lessonService.getAll();
    }

    @GetMapping("get-lesson/{id}")
    public Lesson getLessonById(@PathVariable UUID id) {
        return lessonService.getLesson(id);
    }
}
