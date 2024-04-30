package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.AttendanceRequest;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.AttendanceRepository;
import uz.rivoj.education.repository.LessonRepository;
import uz.rivoj.education.repository.UserRepository;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final LessonRepository lessonRepository;

    public  AttendanceResponse getAttendance(UUID id) {
        AttendanceEntity attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Attendance not found!"));
        return modelMapper.map(attendance,AttendanceResponse.class);
    }

    public String delete(UUID id) {
        attendanceRepository.deleteById(id);
        return "Successfully Deleted!";
    }
    public List<AttendanceResponse> getAllUserAttendance(UUID userId) {
        List<AttendanceEntity> studentAttendances = attendanceRepository.findAllByStudentId(userId);
        return modelMapper.map(studentAttendances, new TypeToken<List<AttendanceResponse>>() {}.getType());
    }
    public AttendanceResponse create(AttendanceRequest attendance) {
        userRepository.findById(attendance.getStudentId()).orElseThrow(() -> new DataNotFoundException("Student not found! " + attendance.getStudentId()));
        userRepository.findById(attendance.getStudentId()).orElseThrow(() -> new DataNotFoundException("Teacher not found! " + attendance.getTeacherId()));
        lessonRepository.findById(attendance.getLessonId()).orElseThrow(() -> new DataNotFoundException("Lesson not found! " + attendance.getLessonId()));
        AttendanceEntity attendanceEntity = modelMapper.map(attendance, AttendanceEntity.class);
        return modelMapper.map(attendanceEntity,AttendanceResponse.class);
    }

    public AttendanceResponse getAttendanceByLesson(UUID userId, UUID lessonId) {
        lessonRepository.findById(lessonId).orElseThrow(() -> new DataNotFoundException("Lesson not found! " + lessonId));
        userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Student not found! " + userId));
        return null;
//        AttendanceResponse attendanceResponse = attendanceRepository.
    }
    public List<AttendanceResponse> getAllAttendanceByStatus(int page, int size, AttendanceStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        List<AttendanceEntity> attendanceEntityList = attendanceRepository.findAllByStatus(pageable, status).getContent();
        return modelMapper.map(attendanceEntityList, new TypeToken<List<AttendanceResponse>>(){}.getType());
    }
}
