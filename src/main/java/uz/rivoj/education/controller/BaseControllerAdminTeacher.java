package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.LessonCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.dto.update.LessonUpdateDTO;
import uz.rivoj.education.entity.UserRole;
import uz.rivoj.education.service.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@RestController
@RequestMapping("/api/v1/base")
@RequiredArgsConstructor

public class BaseControllerAdminTeacher {
    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final LessonService lessonService;
    private final ModuleService moduleService;
    private final UserService userService;



    @PostMapping(value = "/create-lesson",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LessonResponse> createLesson(
            @RequestParam("title") String title,
            @RequestParam("teacherId") UUID teacherId,
            @RequestParam("moduleId") UUID moduleId,
            @RequestParam("description") String description,
            @RequestParam("additionalLinks") List<String> additionalLinks,
            @RequestParam("coverOfLesson") MultipartFile coverOfLesson,
            @RequestParam("lessonVideo") MultipartFile lessonVideo
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(new LessonCR(title,teacherId,moduleId,description,additionalLinks,coverOfLesson,lessonVideo)));
    }

    @PutMapping(value ="/update-lesson",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateLesson(
            @RequestParam("id") UUID id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("additionalLinks") List<String> additionalLinks,
            @RequestParam(value = "coverOfLesson",required = false) MultipartFile coverOfLesson,
            @RequestParam(value = "lessonVideo",required = false) MultipartFile lessonVideo

    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.updateLesson(new LessonUpdateDTO(id,title,description,additionalLinks,coverOfLesson,lessonVideo)));
    }

    @GetMapping("/get-all-users-byRole")
    public ResponseEntity<Map<String, Object>> getAllByRole(
            @RequestParam UserRole role,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(userService.getAllByRole(role, searchTerm, pageNumber, pageSize));
    }

    @GetMapping("/get-lesson-by-id{lessonId}")
    public ResponseEntity<LessonResponse> getLesson(
            @PathVariable UUID lessonId){
        return ResponseEntity.ok(lessonService.getLessonById(lessonId));
    }

    @GetMapping("/get-lessons-by-module{moduleId}")
    public List<SpecialLessonResponse> getLessonsByModule(
            @PathVariable UUID moduleId){
        return lessonService.getLessonsByModule(moduleId);
    }

    @PostMapping("/add-module")
    public ResponseEntity<?> createModule(@RequestParam UUID subjectId,@RequestParam Integer moduleNumber){
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.addModule(subjectId,moduleNumber));
    }

    @GetMapping("/getAllModulesOfSubject{subjectId}")
    public ResponseEntity<List<ModuleResponse>> getAllModulesOfSubject(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(moduleService.getAllModulesOfSubject(subjectId));
    }

    @DeleteMapping("/delete-module{moduleId}")
    public ResponseEntity<String> deleteModule(@PathVariable UUID moduleId){
        return ResponseEntity.status(200).body(moduleService.delete(moduleId));
    }
    @DeleteMapping("/delete-lesson{lessonId}")
    public ResponseEntity<String> deleteLesson(@PathVariable UUID lessonId){
        return ResponseEntity.status(200).body(lessonService.delete(lessonId));
    }



    @GetMapping("/getStatistics2")
    public ResponseEntity<Map<String, Object>> getStatistics2(
            @RequestParam(required = false) UUID moduleId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(studentService.getStatistics2(moduleId, searchTerm, pageNumber, pageSize));
    }
    @DeleteMapping("/delete-attendance{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable UUID id){
        return ResponseEntity.status(200).body(attendanceService.delete(id));
    }

    @GetMapping("/get-attendance/{id}")
    public ResponseEntity<AttendanceSpecialResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.findByAttendanceId(id));
    }
    @PutMapping("/change-ModuleNumber/{moduleId}/{moduleNumber}")
    public ResponseEntity<String> changeModuleNumber(
            @PathVariable UUID moduleId,
            @PathVariable Integer moduleNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.changeModuleNumber(moduleId,moduleNumber));
    }

    @GetMapping("/get-UserDetails-byRole")
    public ResponseEntity<Map<String, Object>> getAllByRole(
            @RequestParam UserRole role,
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return ResponseEntity.ok(userService.getUsersByRoleAndSubjectId(role, subjectId, pageable));
    }


}
