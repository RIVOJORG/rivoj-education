package uz.rivoj.education.service;

import jakarta.persistence.Version;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.rivoj.education.dto.response.AttendanceResponse;
import uz.rivoj.education.dto.response.AttendanceSpecialResponse;
import uz.rivoj.education.dto.response.UncheckedAttendanceResponse;
import uz.rivoj.education.dto.update.CheckAttendanceDTO;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.time.LocalDateTime;
import java.util.*;

import static uz.rivoj.education.entity.enums.AttendanceStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StudentInfoRepository studentRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final ModelMapper modelMapper;



    @Transactional
    public AttendanceSpecialResponse findByAttendanceById(UUID attendanceId) {
        AttendanceEntity attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new DataNotFoundException("Attendance not found with this id: " + attendanceId));
        AttendanceSpecialResponse response = attendanceRepository.findAttendanceDetailsById(attendanceId);
        response.setAnswers(attendance.getAnswers());
        if(attendance.getTeacher() != null) {
            TeacherInfo teacherInfo = attendance.getTeacher();
            response.setTeacherAvatar(teacherInfo.getTeacher().getAvatar());
            response.setTeacherName(teacherInfo.getTeacher().getName());
            response.setTeacherSurname(teacherInfo.getTeacher().getSurname());
        }
        response.setCoin(attendance.getCoin());
        response.setScore(attendance.getScore());
        response.setFeedBack(attendance.getFeedBack());
        return response;
    }

    public String delete(UUID id) {
        attendanceRepository.deleteById(id);
        return "Successfully Deleted!";
    }





    @Version
    @SneakyThrows
    public String checkAttendance(CheckAttendanceDTO checkAttendanceDTO, UUID teacherId) {
        TeacherInfo teacher = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found! " + teacherId));

        AttendanceEntity attendanceEntity = attendanceRepository.findById(checkAttendanceDTO.getAttendanceId())
                .orElseThrow(() -> new DataNotFoundException("Attendance not found with this id: " + checkAttendanceDTO.getAttendanceId()));


        if (attendanceEntity.getStatus() == CHECKED) {
            throw new DataAlreadyExistsException("Attendance has already been checked");
        }

        StudentInfo studentInfo = attendanceEntity.getStudent();
        if (studentInfo == null) {
            throw new DataNotFoundException("Student not found for this attendance");
        }

        int currentCoin = studentInfo.getCoin() != null ? studentInfo.getCoin() : 0;
        int totalScore = studentInfo.getTotalScore() != null ? studentInfo.getTotalScore() : 0;
        int score = checkAttendanceDTO.getScore();


        attendanceEntity.setStatus(CHECKED);
        attendanceEntity.setScore(score);
        attendanceEntity.setFeedBack(checkAttendanceDTO.getFeedBack());
        attendanceEntity.setUpdatedDate(LocalDateTime.now());
        attendanceEntity.setTeacher(teacher);

        if (score < 70) {
            int coinPenalty = (70 - score) / 10;
            attendanceEntity.setCoin(attendanceEntity.getCoin() - coinPenalty);
            currentCoin -= coinPenalty;
        } else {
            int coinBonus = (score + 5) / 10;
            attendanceEntity.setCoin(attendanceEntity.getCoin() + coinBonus);
            currentCoin += coinBonus;
            totalScore += score;
        }


        studentInfo.setCoin(currentCoin);
        studentInfo.setTotalScore(totalScore);

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


    public List<UncheckedAttendanceResponse> getUncheckedAttendances(UUID teacherId) {
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new DataNotFoundException("Teacher not found with this id: " + teacherId));
        List<UncheckedAttendanceResponse> attendanceResponseList = new ArrayList<>();
        Optional<List<AttendanceEntity>> uncheckedAttendanceList = attendanceRepository.findUncheckedAttendanceBySubjectId(teacherInfo.getSubject().getId());
        uncheckedAttendanceList.ifPresent(attendanceEntities -> attendanceEntities.forEach(attendanceEntity -> {
            UncheckedAttendanceResponse attendanceResponse = new UncheckedAttendanceResponse();
            attendanceResponse.setAttendanceId(attendanceEntity.getId());
            attendanceResponse.setAnswerList(attendanceEntity.getAnswers());
            attendanceResponse.setModuleNumber(attendanceEntity.getStudent().getCurrentModule().getNumber());
            attendanceResponse.setAvatar(attendanceEntity.getStudent().getStudent().getAvatar());
            attendanceResponse.setLessonNumber(attendanceEntity.getLesson().getNumber());
            attendanceResponse.setStudentName(attendanceEntity.getStudent().getStudent().getName());
            attendanceResponse.setStudentSurname(attendanceEntity.getStudent().getStudent().getSurname());
            attendanceResponse.setStudentId(attendanceEntity.getStudent().getStudent().getId());
            attendanceResponse.setAvatar(attendanceEntity.getStudent().getStudent().getAvatar());
            attendanceResponseList.add(attendanceResponse);
        }));
        return attendanceResponseList;


    }
}
