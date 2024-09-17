package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rivoj.education.dto.request.AttendanceCR;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.dto.update.CheckAttendanceDTO;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static uz.rivoj.education.entity.enums.AttendanceStatus.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final StudentInfoRepository studentRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final ModelMapper modelMapper;
    private final LessonRepository lessonRepository;

    public AttendanceResponse getAttendance(UUID id) {
        AttendanceEntity attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Attendance not found!"));
        return modelMapper.map(attendance, AttendanceResponse.class);
    }

    public AttendanceResponse findByAttendanceId(UUID attendanceId) {
        AttendanceEntity attendanceEntity = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new DataNotFoundException("Attendance not found with this id: " + attendanceId));

        return AttendanceResponse.builder()
                .coin(attendanceEntity.getCoin())
                .feedBack(attendanceEntity.getFeedBack())
                .status(attendanceEntity.getStatus())
                .lesson_id(attendanceEntity.getLesson().getId())
                .answers(attendanceEntity.getAnswers())
                .score(attendanceEntity.getScore())
                .student_id(attendanceEntity.getStudent().getId())
                .teacher_id(attendanceEntity.getTeacher().getId())
                .answers(attendanceEntity.getAnswers())
                .build();
    }

    public String delete(UUID id) {
        attendanceRepository.deleteById(id);
        return "Successfully Deleted!";
    }

    public List<AttendanceResponse> getAllUserAttendance(UUID userId) {
        List<AttendanceEntity> studentAttendances = attendanceRepository.findAllByStudentId(userId);
        List<AttendanceResponse> attendanceResponseList = new ArrayList<>();
        for (AttendanceEntity studentAttendance : studentAttendances) {
            attendanceResponseList.add(AttendanceResponse.builder()
                    .coin(studentAttendance.getCoin())
                    .feedBack(studentAttendance.getFeedBack())
                    .status(studentAttendance.getStatus())
                    .lesson_id(studentAttendance.getLesson().getId())
                    .answers(studentAttendance.getAnswers())
                    .score(studentAttendance.getScore())
                    .student_id(studentAttendance.getStudent().getId())
                    .teacher_id(studentAttendance.getTeacher().getId())
                    .answers(studentAttendance.getAnswers())
                    .build());
        }
        return attendanceResponseList;

    }
//    public AttendanceResponse create(AttendanceCR attendance, UUID userId) {
//        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found! " + userId));
//        StudentInfo studentInfo = studentRepository.findStudentInfoByStudentId(user.getId()).orElseThrow(() -> new DataNotFoundException("Student not found! " + userId));
//        TeacherInfo teacherInfo = teacherInfoRepository.findById(attendance.getTeacherId()).orElseThrow(() -> new DataNotFoundException("Teacher not found! " + attendance.getTeacherId()));
//        LessonEntity lessonEntity = lessonRepository.findById(attendance.getLessonId()).orElseThrow(() -> new DataNotFoundException("Lesson not found! " + attendance.getLessonId()));
//        AttendanceEntity attendanceEntity = AttendanceEntity.builder()
//                .answer(attendance.getAnswer())
//                .coin(0)
//                .lessonEntity(lessonEntity)
//                .student(studentInfo)
//                .teacher(teacherInfo)
//                .status(UNCHECKED)
//                .build();
//        attendanceRepository.save(attendanceEntity);
//        return AttendanceResponse.builder()
//                .attendanceId(attendanceEntity.getId())
//                .coin(attendanceEntity.getCoin())
//                .feedBack(attendanceEntity.getFeedBack())
//                .isCorrect(true)
//                .lessonId(attendanceEntity.getLessonEntity().getId())
//                .answers(attendanceEntity.getAnswer())
//                .score(attendanceEntity.getScore())
//                .studentId(attendanceEntity.getStudent().getId())
//                .teacherId(attendanceEntity.getTeacher().getId())
//                .video(attendanceEntity.getLessonEntity().getCover())
//                .build();
//    }

    public List<AttendanceResponse> getAttendancesByLesson(UUID lessonId) {
        LessonEntity lessonEntity = lessonRepository.findById(lessonId).orElseThrow(() -> new DataNotFoundException("Lesson not found! " + lessonId));
        List<AttendanceResponse> attendanceResponseList = new ArrayList<>();
        List<AttendanceEntity> attendanceByLessonEntity = attendanceRepository.findAttendanceByLesson_Id(lessonEntity.getId());
        for (AttendanceEntity studentAttendance : attendanceByLessonEntity) {
            attendanceResponseList.add(AttendanceResponse.builder()
                    .coin(studentAttendance.getCoin())
                    .feedBack(studentAttendance.getFeedBack())
                    .status(studentAttendance.getStatus())
                    .lesson_id(studentAttendance.getLesson().getId())
                    .answers(studentAttendance.getAnswers())
                    .score(studentAttendance.getScore())
                    .student_id(studentAttendance.getStudent().getId())
                    .teacher_id(studentAttendance.getTeacher().getId())
                    .answers(studentAttendance.getAnswers())
                    .build());
        }

        return attendanceResponseList;
    }


    public List<AttendanceResponse> getAllAttendanceByStatus(int page, int size, AttendanceStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        List<AttendanceResponse> attendanceResponseList = new ArrayList<>();
        for (AttendanceEntity attendanceEntity : attendanceRepository.findAllByStatus(pageable, status).getContent()) {
            AttendanceResponse attendanceResponse = AttendanceResponse.builder()
                    .coin(attendanceEntity.getCoin())
                    .feedBack(attendanceEntity.getFeedBack())
                    .status(attendanceEntity.getStatus())
                    .lesson_id(attendanceEntity.getLesson().getId())
                    .answers(attendanceEntity.getAnswers())
                    .score(attendanceEntity.getScore())
                    .student_id(attendanceEntity.getStudent().getId())
                    .teacher_id(attendanceEntity.getTeacher().getId())
                    .answers(attendanceEntity.getAnswers())
                    .build();
            attendanceResponseList.add(attendanceResponse);
        }
        return attendanceResponseList;
    }
        @SneakyThrows
    public String checkAttendance(CheckAttendanceDTO checkAttendanceDTO,UUID teacherId) {
        TeacherInfo teacher = teacherInfoRepository.findById(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found! " + teacherId));
        AttendanceEntity attendanceEntity = attendanceRepository.findById(checkAttendanceDTO.getAttendanceId())
                .orElseThrow(() -> new DataNotFoundException("Attendance not found with this id: " + checkAttendanceDTO.getAttendanceId()));
        if (attendanceEntity.getStatus() == CHECKED) {
            throw new DataAlreadyExistsException("Attendance has already been checked");
        }
        int score = checkAttendanceDTO.getScore();
        StudentInfo studentInfo = attendanceEntity.getStudent();
        if (studentInfo == null) {
            throw new DataNotFoundException("Student not found for this attendance");
        }
        int currentCoin = studentInfo.getCoin() != null ? studentInfo.getCoin() : 0;
        int totalScore = studentInfo.getTotalScore() != null ? studentInfo.getTotalScore() : 0;

        if (score < 70) {
            attendanceEntity.setStatus(NOT_LOADED);
            attendanceEntity.setCoin(attendanceEntity.getCoin() - ((70 - score) / 10));
            currentCoin -= (70 - score) / 10;
        } else {
            attendanceEntity.setStatus(CHECKED);
            attendanceEntity.setScore(score);
            attendanceEntity.setCoin(attendanceEntity.getCoin() + ((score + 5) / 10));
            currentCoin += (score + 5) / 10;
            totalScore += score;
        }
        studentInfo.setCoin(currentCoin);
        studentInfo.setTotalScore(totalScore);
        attendanceEntity.setFeedBack(checkAttendanceDTO.getFeedBack());
        attendanceEntity.setUpdatedDate(LocalDateTime.now());
        attendanceEntity.setTeacher(teacher);
        studentRepository.save(studentInfo);
        attendanceRepository.save(attendanceEntity);
        return "Attendance successfully checked";

    }

    public static final String ANSI_RED = "\u001B[31m";
    @Transactional
    public List<AttendanceResponse> getAttendanceByLessonId(UUID userId, UUID lessonId) {
        StudentInfo student = studentRepository.findStudentInfoByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id: " + userId));
        List<AttendanceEntity> attendanceEntityList = attendanceRepository.findByStudent_IdAndLesson_Id(student.getId(), lessonId)
                .orElseThrow(() -> new DataNotFoundException("Attendance not found!"));

        System.out.println(ANSI_RED + "attendanceEntityList.get(0).getAnswers() = " + attendanceEntityList.get(0).getAnswers());
        return attendanceEntityList.stream()
                .map(entity -> modelMapper.map(entity, AttendanceResponse.class))
                .collect(Collectors.toList());
    }
}
