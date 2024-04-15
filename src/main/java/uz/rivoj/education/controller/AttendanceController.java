package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.AttendanceRequest;
import uz.rivoj.education.dto.request.LessonCreateRequest;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.dto.response.LessonResponse;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.service.AttendanceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/create")
    public ResponseEntity<AttendanceResponse> createAttendance(@RequestBody AttendanceRequest attendance){
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(attendance));
    }
    @DeleteMapping("/delete{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable UUID id){
        return ResponseEntity.status(200).body(attendanceService.delete(id));
    }

    @GetMapping("/get-all{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAllUserAttendance(@PathVariable UUID userId){
        return ResponseEntity.ok(attendanceService.getAllUserAttendance(userId));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.getAttendance(id));
    }
    @GetMapping("/get-attendance-by-lesson")
    public ResponseEntity<AttendanceResponse> getAttendanceByLesson(@PathVariable UUID userId,UUID lessonId){
        return ResponseEntity.ok(attendanceService.getAttendanceByLesson(userId,lessonId));
    }

}
