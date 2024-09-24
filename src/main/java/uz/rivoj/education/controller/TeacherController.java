package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.LessonCR;
import uz.rivoj.education.dto.request.TeacherUpdate;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.dto.update.CheckAttendanceDTO;
import uz.rivoj.education.dto.update.LessonUpdateDTO;
import uz.rivoj.education.entity.ChatEntity;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.service.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final LessonService lessonService;
    private final ChatService chatService;
    private final ModuleService moduleService;
    private final TeacherService teacherService;

    @PostMapping(value = "/create-lesson", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LessonResponse> createLesson(
            Principal principal,
            @ModelAttribute LessonCR createRequest,
            @RequestPart("lessonVideo") MultipartFile lessonVideo,
            @RequestPart("coverOfLesson") MultipartFile coverOfLesson
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(createRequest, lessonVideo,coverOfLesson,UUID.fromString(principal.getName())));
    }

    @PutMapping(value ="/update-lesson",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateLesson(
            @RequestBody LessonUpdateDTO updateDTO,
            @RequestPart(required = false) MultipartFile videoFile,
            @RequestPart(required = false) MultipartFile coverOfLesson
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.updateLesson(updateDTO,videoFile,coverOfLesson));
    }

    @GetMapping("/get-my-all-student")
    public ResponseEntity<List<StudentResponse>> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal)
        {
        return ResponseEntity.status(200).body(studentService.getAllMyStudent(page, size, UUID.fromString(principal.getName())));
    }


    @DeleteMapping("/delete-attendance{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable UUID id){
        return ResponseEntity.status(200).body(attendanceService.delete(id));
    }

    @GetMapping("/get-attendance-by-status") // for mentor and admin
    public List<AttendanceResponse> getAllAttendanceByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam AttendanceStatus status){
        return attendanceService.getAllAttendanceByStatus(page, size, status);
    }

    @PutMapping("/check-attendance")
    public ResponseEntity<String> checkAttendance(@RequestBody CheckAttendanceDTO checkAttendanceDTO,Principal principal) {
        return ResponseEntity.status(200).body(attendanceService.checkAttendance(checkAttendanceDTO,UUID.fromString(principal.getName())));
    }

    @GetMapping("/get-all-attendance-by-userId{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAllUserAttendance(@PathVariable UUID userId){
        return ResponseEntity.ok(attendanceService.getAllUserAttendance(userId));
    }
    @GetMapping("/get-attendance/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.findByAttendanceId(id));
    }


    @GetMapping("get-chat/{id}")
    public ChatEntity getChatById(@PathVariable UUID id) {
        return chatService.getChat(id);
    }


    @GetMapping("/get-lessons-by-module{moduleId}") // for mentor and admin
    public List<LessonResponse> getLessonsByModule(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable UUID moduleId){
        return lessonService.getLessonsByModule(page, size, moduleId);
    }
    @GetMapping("get-lesson/{id}")
    public LessonResponse getLessonById(@PathVariable UUID id) {
        return lessonService.findByLessonId(id);
    }


    @GetMapping("get-module/{id}")
    public ModuleResponse getModuleById(@PathVariable UUID id) {
        return moduleService.findByModuleId(id);
    }


    @GetMapping("/getStatisticsByModule")
    public ResponseEntity<List<StudentStatisticsDTO>> getStudentStatisticsByModule(
            Principal principal,
            @RequestParam UUID moduleId
    ) {
        return ResponseEntity.ok(studentService.getStudentStatisticsByModule(UUID.fromString(principal.getName()), moduleId));
    }

    @GetMapping("/getAllStatisticsOnCurrentModule")
    public ResponseEntity<List<StudentStatisticsDTO>> getAllStudentStatisticsOnCurrentModule(
            Principal principal
    ) {
        return ResponseEntity.ok(studentService.getAllStudentStatisticsOnCurrentModule(UUID.fromString(principal.getName())));
    }

    @PutMapping(value = "/update_profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherResponse> createComment(
            @ModelAttribute TeacherUpdate teacherUpdate,
            Principal principal,
            @RequestPart(value = "ProfilePicture",required = false) MultipartFile picture){
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.updateProfile(teacherUpdate,picture, UUID.fromString(principal.getName())));
    }
    @PostMapping("/create-module")
    public ResponseEntity<ModuleResponse> createModule(@RequestBody Integer moduleNumber, Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.create(moduleNumber, UUID.fromString(principal.getName())));
    }
    @GetMapping("/getAllModulesOfSubject{subjectId}")
    public ResponseEntity<List<ModuleResponse>> getAllModulesOfSubject(@PathVariable UUID subjectId){
        return ResponseEntity.status(200).body(moduleService.getAllModulesOfSubject(subjectId));
    }
    @GetMapping("/getAllLessonsByModule{moduleId}")
    public ResponseEntity<List<LessonResponse>> getAllLessonsByModule(@PathVariable UUID moduleId){
        return ResponseEntity.status(200).body(moduleService.getAllLessonsByModule(moduleId));
    }
    @GetMapping("/getUncheckedAttendanceResponse{attendanceId}")
    public ResponseEntity<UncheckedAttendanceResponse> getUncheckedAttendanceResponse(@PathVariable UUID attendanceId){
        return ResponseEntity.status(200).body(attendanceService.getUncheckedAttendanceResponse(attendanceId));
    }
}
