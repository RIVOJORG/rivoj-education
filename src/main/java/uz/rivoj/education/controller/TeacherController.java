package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.TeacherUpdate;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.dto.update.CheckAttendanceDTO;
import uz.rivoj.education.service.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final AttendanceService attendanceService;
    private final TeacherService teacherService;
    private final UserService userService;
    private final ModuleService moduleService;

    @PutMapping("/check-attendance")
    public ResponseEntity<String> checkAttendance(@RequestBody CheckAttendanceDTO checkAttendanceDTO,Principal principal) {
        return ResponseEntity.status(200).body(attendanceService.checkAttendance(checkAttendanceDTO,UUID.fromString(principal.getName())));
    }


    @PutMapping(value = "/update_profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherResponse> updateProfile(
            @ModelAttribute TeacherUpdate teacherUpdate,
            Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.updateProfile(teacherUpdate, UUID.fromString(principal.getName())));
    }

    @GetMapping("/get-unchecked-attendances")
    public ResponseEntity<List<UncheckedAttendanceResponse>> getUncheckedAttendancesBySubjectId(Principal principal){
        return ResponseEntity.status(200).body(attendanceService.getUncheckedAttendances(UUID.fromString(principal.getName())));
    }

    @GetMapping("/get-students")
    public ResponseEntity<Map<String, Object>> getStudentsBySubject(
            Principal principal,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(userService.getStudentsBySubject(UUID.fromString(principal.getName()), searchTerm, pageNumber, pageSize));
    }

    @GetMapping("/getAllModules")
    public ResponseEntity<List<ModuleResponse>> getAllModulesOfSubject(Principal principal){
        return ResponseEntity.status(200).body(moduleService.getAllModulesOfSubjectForTeacher(UUID.fromString(principal.getName())));
    }

}
