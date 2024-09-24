package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.dto.response.UncheckedAttendanceResponse;
import uz.rivoj.education.dto.update.CheckAttendanceDTO;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.time.LocalDateTime;
import java.util.*;

import static uz.rivoj.education.entity.enums.AttendanceStatus.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
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
        Optional<List<AttendanceEntity>> attendanceEntityList = attendanceRepository.findByStudentId(userId);
        return getAttendanceResponses(attendanceEntityList);

    }

    private List<AttendanceResponse> getAttendanceResponses(Optional<List<AttendanceEntity>> attendanceEntityList) {
        if(attendanceEntityList.isPresent()) {
            List<AttendanceResponse> attendanceResponseList = new ArrayList<>();
            List<AttendanceEntity> attendanceEntities = attendanceEntityList.get();
            for (AttendanceEntity studentAttendance : attendanceEntities) {
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

        }else {
            throw new DataNotFoundException("Attendance not found!");
        }
    }

    public AttendanceResponse getAttendancesByLesson(UUID lessonId) {
        LessonEntity lessonEntity = lessonRepository.findById(lessonId).orElseThrow(() -> new DataNotFoundException("Lesson not found! " + lessonId));
        Optional<AttendanceEntity> attendanceEntity = attendanceRepository.findByLesson_Id(lessonEntity.getId());
        if(attendanceEntity.isPresent()){
            return modelMapper.map(attendanceEntity,AttendanceResponse.class);
        }
        throw new DataNotFoundException("Attendance not found!");
    }


    public List<AttendanceResponse> getAllAttendanceByStatus(int page, int size, AttendanceStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Optional<Page<AttendanceEntity>> attendanceEntityList = attendanceRepository.findByStatus(pageable, status);
        return getAttendanceResponses(attendanceEntityList.map(Page::getContent));

    }
    @SneakyThrows
    public String checkAttendance(CheckAttendanceDTO checkAttendanceDTO,UUID teacherId) {
        TeacherInfo teacher = teacherInfoRepository.findByTeacher_Id(teacherId)
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
            attendanceEntity.setStatus(CHECKED);
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

    @Transactional
    public AttendanceResponse getAttendanceByLessonId(UUID userId, UUID lessonId) {
        StudentInfo student = studentRepository.findByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found with this id: " + userId));
        AttendanceEntity attendanceEntity = attendanceRepository.findByStudentIdAndLessonId(student.getId(), lessonId)
                .orElseThrow(() -> new DataNotFoundException("Attendance not found!"));
        AttendanceResponse attendanceResponse = modelMapper.map(attendanceEntity, AttendanceResponse.class);
        attendanceResponse.setLesson_id(attendanceEntity.getLesson().getId());
        attendanceResponse.setStudent_id(attendanceEntity.getStudent().getId());
        if(attendanceEntity.getTeacher() != null){
            attendanceResponse.setTeacher_id(attendanceEntity.getTeacher().getId());
        }
        return attendanceResponse;
    }

    public UncheckedAttendanceResponse getUncheckedAttendanceResponse(UUID attendanceId) {
        AttendanceEntity attendanceEntity = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new DataNotFoundException("Attendance not found with this id: " + attendanceId));
       return UncheckedAttendanceResponse.builder()
                .attendanceId(attendanceEntity.getId())
                .attendanceSource(attendanceEntity.getLesson().getSource())
                .attendanceCover(attendanceEntity.getLesson().getCover())
                .currentModule(attendanceEntity.getStudent().getCurrentModule().getNumber())
                .currentLesson(attendanceEntity.getStudent().getLesson().getNumber())
                .studentName(attendanceEntity.getStudent().getStudent().getName())
                .studentSurname(attendanceEntity.getStudent().getStudent().getSurname()).build();
    }
}
