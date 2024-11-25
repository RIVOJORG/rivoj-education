package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.*;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.service.*;
import uz.rivoj.education.service.firebase.FirebaseService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final StudentService studentService;
    private final UserService userService;
    private final TeacherService teacherService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final SubjectService subjectService;
    private final FirebaseService firebaseService;


    @PostMapping("/add-student")
    public ResponseEntity<String> addStudent(@RequestBody StudentCR studentCR){
        return studentService.addStudent(studentCR);
    }
    @PostMapping("/add-teacher")
    public ResponseEntity<String> addTeacher(@RequestBody TeacherCR teacherInfo){
        return teacherService.createTeacher(teacherInfo);
    }
    @PostMapping("/add-admin")
    public ResponseEntity<String> addAdmin(@RequestBody UserCR adminDto) throws ExecutionException, InterruptedException {
        return userService.addAdmin(adminDto);
    }

    @PostMapping("/create-subject")
    public ResponseEntity<SubjectResponse> createSubject(@RequestBody SubjectCR createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(createRequest));
    }

    @PutMapping("/update-role{userPhoneNumber}")
    public ResponseEntity<String> updateRole(@PathVariable String userPhoneNumber, @RequestParam UserRole userRole){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userPhoneNumber, userRole));
    }

    @PutMapping("/change-phoneNumber/{userId}/{newPhoneNumber}")
    public ResponseEntity<String> changePhoneNumber(
            @PathVariable UUID userId,
            @PathVariable String newPhoneNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePhoneNumber(userId, newPhoneNumber));
    }
    @PutMapping("/change-password/{userId}/{newPassword}")
    public ResponseEntity<String> changePassword(
            @PathVariable UUID userId,
            @PathVariable String newPassword) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePassword(userId,newPassword));
    }

    @GetMapping("/get-all-subjects")
    public List<SubjectResponse> getAllSubject(){
        return subjectService.getAll();
    }

    @PostMapping("/block-unblock-user{userId}")
    public ResponseEntity<String> blockUnblockUser(@PathVariable UUID userId, @RequestParam UserStatus status){
        return ResponseEntity.status(200).body(userService.blockUnblockUser(userId, status));
    }

    @DeleteMapping("/delete-subject{subjectId}")
    public ResponseEntity<String> deleteSubject(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(subjectService.delete(subjectId));
    }


    @DeleteMapping("/delete-comment{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable UUID commentId){
        return ResponseEntity.status(200).body(commentService.delete(commentId));
    }
    @DeleteMapping("delete-user{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId){
        return ResponseEntity.status(204).body(userService.deleteUser(userId));
    }


    @GetMapping("/getTeachers")
    public ResponseEntity<List<TeacherDTO>> getTeachers(){
        return ResponseEntity.status(200).body(userService.getTeachers());
    }


    @PutMapping("/change-SubjectTitle/{subjectId}/{subjectName}")
    public ResponseEntity<String> changeSubjectTitle(
            @PathVariable UUID subjectId,
            @PathVariable String subjectName) {
        return ResponseEntity.status(HttpStatus.OK).body(subjectService.changeSubjectTitle(subjectId,subjectName));
    }

    @PutMapping(value = "/update-profile")
    public ResponseEntity<String > updateProfile(
            @ModelAttribute AdminCR adminUpdate,
            Principal principal
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.updateProfile(adminUpdate, UUID.fromString(principal.getName())));
    }








}
