package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.StudentCreateRequest;
import uz.rivoj.education.dto.request.TeacherInfoRequest;
import uz.rivoj.education.dto.response.GetStudentFullInfoResponse;
import uz.rivoj.education.dto.response.StudentResponse;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.service.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final StudentService studentService;
    private final LessonService lessonService;
    private final UserService userService;
    private final TeacherService teacherService;
    private final ProgressService progressService;

    @PostMapping("/create-teacher")
    public ResponseEntity<String> createTeacher(@RequestBody TeacherInfoRequest teacherInfo){
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(teacherInfo));
    }


    //    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-all-student")
    public ResponseEntity<List<StudentResponse>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(200).body(studentService.getAll(page, size));
    }


    //    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/get-lessons-by-module{moduleId}") // for mentor and admin
    public List<LessonResponse> getLessonsByModule(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID moduleId){
        return lessonService.getLessonsByModule(page, size, moduleId);
    }

    @PostMapping("/add-student")
    public ResponseEntity<String> addStudent(StudentCreateRequest studentCreateRequest){
        return ResponseEntity.status(200).body(studentService.addStudent(studentCreateRequest));
    }

    @PutMapping("/change-phoneNumber/{oldPhoneNumber}/{newPhoneNumber}")
    public ResponseEntity<String> changePhoneNumber(
            @PathVariable String oldPhoneNumber,
            @PathVariable String newPhoneNumber) {
        return ResponseEntity.status(200).body(userService.changePhoneNumber(oldPhoneNumber, newPhoneNumber));
    }

    @GetMapping("/block-unblock-user{phoneNumber}")
    public ResponseEntity<String> blockUnblockUser(@PathVariable String phoneNumber, @RequestParam UserStatus status){
        return ResponseEntity.status(200).body(userService.blockUnblockUser(phoneNumber, status));
    }

    @GetMapping("/get-student-full-info{phoneNumber}")
    public ResponseEntity<GetStudentFullInfoResponse> getStudentFullInfoResponse(@PathVariable String phoneNumber){
        return ResponseEntity.status(200).body(progressService.getStudentFullInfoResponse(phoneNumber));

    }


}
