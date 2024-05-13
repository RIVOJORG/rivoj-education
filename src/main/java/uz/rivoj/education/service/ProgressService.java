package uz.rivoj.education.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final StudentInfoRepository studentInfoRepository;
    private final DiscountRepository discountRepository;
    private final AttendanceRepository attendanceRepository;
    private final CommentRepository commentRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public HomePageResponse getProgressByPhoneNumber(String phoneNumber) {

        UserEntity userEntity = userRepository.findUserEntityByPhoneNumber(phoneNumber)
                .orElseThrow(
                        () -> new DataNotFoundException("data not found")
                );

        StudentInfo studentInfo = studentInfoRepository.findStudentInfoByStudentId(userEntity.getId())
                .orElseThrow(
                        () -> new DataNotFoundException("data not found")
                );

        List<AttendanceEntity> attendancesOfModule = attendanceRepository.findAttendanceEntitiesByStudentIdAndLessonEntity_Module(
                userEntity.getId(),
                studentInfo.getCurrentModule()
        );

        List<Integer> scores = new ArrayList<>();
        for (AttendanceEntity attendance : attendancesOfModule) {
            if (attendance.getStatus() == AttendanceStatus.CHECKED) {
                scores.add(attendance.getLessonEntity().getNumber(), attendance.getScore());
            }
        }

        List<DiscountResponse> discounts = discountRepository.findDiscountEntitiesByStudentId(userEntity.getId())
                .stream().map(discount -> modelMapper.map(discount, DiscountResponse.class))
                .collect(Collectors.toList());

        return HomePageResponse.builder()
                .phoneNumber(userEntity.getPhoneNumber())
                .name(userEntity.getName())
                .surname(userEntity.getSurname())
                .avatar(studentInfo.getAvatar())
                .currentModule(studentInfo.getCurrentModule().getNumber())
                .currentLesson(studentInfo.getLesson().getNumber())
                .isLessonOver(studentInfo.getIsLessonOver())
                .coin(studentInfo.getCoin())
                .totalScore(studentInfo.getTotalScore())
                .scores(scores)
                .discounts(discounts)
                .build();
    }

    public RankingPageResponse getRankingPage() {
        //Sort sortByTotalScoreDesc = Sort.by(Sort.Direction.DESC, "totalScore");
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "totalScore"));
        Page<StudentInfo> page = studentInfoRepository.findAll(pageRequest);
        List<StudentInfo> sortedStudents = page.getContent();

        List<BestStudentResponse> bestStudentResponseList = new ArrayList<>();

        for (StudentInfo studentInfo : sortedStudents) {
            Optional<StudentInfo> studentInfoByStudentId = studentInfoRepository.findStudentInfoByStudentId(studentInfo.getId());
            if (studentInfoByStudentId.isPresent()) {
                UserEntity user = studentInfoByStudentId.get().getStudent();
                BestStudentResponse bestStudent = BestStudentResponse.builder()
                        .avatar(studentInfo.getAvatar())
                        .name(user.getName())
                        .percentage(studentInfo.getTotalScore())
                        .surname(user.getSurname())
                        .build();
                bestStudentResponseList.add(bestStudent);
            }
        }
        return RankingPageResponse.builder()
                .bestStudents(bestStudentResponseList)
                .build();
    }


    public LessonPageResponse getLessonPageResponseByLessonId(UUID studentId, UUID lessonId) {

        LessonEntity lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new DataNotFoundException("lesson not found")
        );

        TeacherInfo teacherInfo = teacherInfoRepository
                .findTeacherInfoBySubjectId(lesson.getModule().getSubject().getId())
                .orElseThrow(
                        () -> new DataNotFoundException("teacher info not found")
                );

        TeacherResponse teacherResponse = TeacherResponse.builder()
                .name(teacherInfo.getTeacher().getName())
                .surname(teacherInfo.getTeacher().getSurname())
                .avatar(teacherInfo.getAvatar())
                .subject(modelMapper.map(teacherInfo.getSubject(), SubjectResponse.class))
                .about(teacherInfo.getAbout())
                .build();

        List<CommentResponse> comments = commentRepository.findCommentEntitiesByLesson_Id(lessonId)
                .stream().map(comment -> modelMapper.map(comment, CommentResponse.class))
                .toList();

        List<AttendanceResponse> attendances = attendanceRepository
                .findAttendanceEntitiesByStudentIdAndLessonEntity(studentId, lesson)
                .stream().map(attendance -> modelMapper.map(attendance, AttendanceResponse.class))
                .toList();

        return LessonPageResponse.builder()
                .source(lesson.getSource())
                .cover(lesson.getCover())
                .teacher(teacherResponse)
                .comments(comments)
                .attendances(attendances).build();
    }

    public EducationPageResponse getEducationPage(UUID studentId) {
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findStudentInfoByStudentId(studentId);

        if (studentInfoOptional.isPresent()) {
            StudentInfo studentInfo = studentInfoOptional.get();

            int totalCountModules = moduleRepository.countBySubject(studentInfo.getSubject());
            List<Integer> countLessonsList = new ArrayList<>();

            for (int i = 1; i <= totalCountModules; i++) {
                List<LessonEntity> lessonsForModule = lessonRepository.findByModuleNumber(i);

                int countLessons = countCompletedLessons(lessonsForModule, studentInfo);
                countLessonsList.add(countLessons);
            }

            return EducationPageResponse.builder()
                    .bestStudentsOfLesson(getRankingPage().getBestStudents())
                    .coin(studentInfo.getCoin())
                    .countModules(totalCountModules)
                    .countLessons(countLessonsList)
                    .currentLesson(studentInfo.getLesson().getNumber())
                    .isLessonOver(studentInfo.getIsLessonOver())
                    .totalScore(studentInfo.getTotalScore())
                    .build();
        } else {
            throw new DataNotFoundException("student not found with this id: " + studentId);
        }
    }

    private int countCompletedLessons(List<LessonEntity> lessonsForModule, StudentInfo studentInfo) {
        int count = 0;
        for (LessonEntity lesson : lessonsForModule) {
            if (lesson.getNumber() <= studentInfo.getLesson().getNumber()) {
                count++;
            }
        }
        return count;
    }
}
