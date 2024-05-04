package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.TeacherInfoRequest;
import uz.rivoj.education.dto.response.StudentResponse;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.entity.TeacherInfo;
import uz.rivoj.education.service.LessonService;
import uz.rivoj.education.service.StudentService;
import uz.rivoj.education.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final StudentService studentService;
    private final LessonService lessonService;
    private final UserService userService;

    @PostMapping("/create-teacher")
    public ResponseEntity<TeacherInfo> createTeacher(@RequestBody TeacherInfoRequest teacherInfo){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createTeacher(teacherInfo));
    }



    //    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-all-student")
    public ResponseEntity<List<StudentResponse>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(200).body(studentService.getAll(page, size));
    }


    //    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-lessons-by-module") // for mentor and admin
    public List<LessonResponse> getLessonsByModule(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID moduleId){
        return lessonService.getLessonsByModule(page, size, moduleId);
    }

}
