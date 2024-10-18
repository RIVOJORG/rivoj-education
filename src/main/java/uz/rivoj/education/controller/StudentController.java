package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.AttendanceCR;
import uz.rivoj.education.dto.request.StudentUpdate;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.service.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {
    private final AttendanceService attendanceService;
    private final ModuleService moduleService;
    private  final StudentService studentService;


    @GetMapping("/getAllLessonsOfModule")
    public ResponseEntity<List<LessonResponse>> getAllLessons(Principal principal,@RequestParam UUID moduleId){
        return ResponseEntity.ok(moduleService.getAllAccessibleLessonsOfUser(UUID.fromString(principal.getName()),moduleId));
    }
    @GetMapping("/getAllModulesOfStudent")
    public ResponseEntity<List<ModuleResponse>> getAllModulesOfStudent(Principal principal) {
        return  ResponseEntity.ok(moduleService.getAllModulesOfStudent(UUID.fromString(principal.getName())));
    }

    @PostMapping(value = "/upload-homework",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> uploadHomework(
            Principal principal,
            @ModelAttribute AttendanceCR attendanceCR,
            @RequestPart List<MultipartFile> files
    ){
        return ResponseEntity.ok(studentService.uploadHomework(attendanceCR, UUID.fromString(principal.getName()), files));
    }

    @PutMapping(value = "/update-profile")
    public ResponseEntity<StudentResponse> updateProfile(
            @ModelAttribute StudentUpdate studentUpdate,
            Principal principal
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.updateProfile(studentUpdate, UUID.fromString(principal.getName())));
    }

    @PutMapping(value = "/update-profile-picture",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<String> updateProfilePicture(
            Principal principal,
            @RequestParam("picture") MultipartFile picture
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.updateProfilePicture(picture, UUID.fromString(principal.getName())));
    }

    @GetMapping("/get-homework-by-lesson-id")
    public  ResponseEntity<AttendanceResponse>  getHomeworkByLessonId(Principal principal,@RequestParam UUID lessonId){
        return ResponseEntity.ok(attendanceService.getAttendanceByLessonId(UUID.fromString(principal.getName()),lessonId));
    }


    @GetMapping("/getProgressByModule")
    public ResponseEntity<List<ProgressResponse>> getProgressByModule(Principal principal){
        return ResponseEntity.ok(studentService.getStudentProgress(UUID.fromString(principal.getName())));
    }

    @PostMapping("/sendPhoneNumber{phoneNumber}")
    public ResponseEntity<Integer> sendPhoneNumber(@PathVariable String phoneNumber){
        return ResponseEntity.ok(studentService.sendOTP(phoneNumber));
    }

    @PostMapping("/checkVerification")
    public ResponseEntity<JwtResponse> checkVerification(@RequestParam Integer code){
        return ResponseEntity.ok(studentService.checkOTP(code));
    }


}
