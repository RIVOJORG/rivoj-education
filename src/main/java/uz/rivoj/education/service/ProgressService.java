package uz.rivoj.education.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private final UserService userService;

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

    public RankingPageResponse getTop10Students() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "totalScore"));
        Page<StudentInfo> page = studentInfoRepository.findAll(pageRequest);
        List<StudentInfo> sortedStudents = page.getContent();

        return mapToBestStudentResponse(sortedStudents);
    }

    public RankingPageResponse getTop10StudentsBySubject(UUID userId) {
        StudentInfo studentInfo = studentInfoRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        List<StudentInfo> list = studentInfoRepository.findTop10BySubjectOrderByTotalScoreDesc(studentInfo.getSubject(), PageRequest.of(0, 10));
        return mapToBestStudentResponse(list);
    }

    private RankingPageResponse mapToBestStudentResponse(List<StudentInfo> list) {
        List<BestStudentResponse> bestStudentResponseList = new ArrayList<>();

        for (StudentInfo s : list) {
            UserEntity user = s.getStudent();
            BestStudentResponse bestStudent = BestStudentResponse.builder()
                    .avatar(s.getAvatar())
                    .name(user.getName())
                    .percentage(s.getTotalScore())
                    .surname(user.getSurname())
                    .build();
            bestStudentResponseList.add(bestStudent);
        }
        return RankingPageResponse.builder()
                .bestStudents(bestStudentResponseList).build();
    }


    public LessonPageResponse getLessonPageResponseByLessonId(String userId, UUID lessonId) {
        UserEntity student = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentInfo studentInfo = studentInfoRepository.findByStudent(student);
        if (studentInfo == null) {
            throw new RuntimeException("Student information not found");
        }

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
                .findAttendanceEntitiesByStudentIdAndLessonEntity(UUID.fromString(String.valueOf(studentInfo.getId())), lesson)
                .stream().map(attendance -> modelMapper.map(attendance, AttendanceResponse.class))
                .toList();

        return LessonPageResponse.builder()
                .source(lesson.getSource())
                .cover(lesson.getCover())
                .teacher(teacherResponse)
                .comments(comments)
                .attendances(attendances).build();
    }

    public EducationPageResponse getEducationPage(UUID userId) {
        UserEntity student = userRepository.findById(UUID.fromString(String.valueOf(userId)))
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentInfo studentInfoEntity = studentInfoRepository.findByStudent(student);
        if (studentInfoEntity == null) {
            throw new RuntimeException("Student information not found");
        }
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findStudentInfoByStudentId(studentInfoEntity.getId());

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
                    .bestStudentsOfLesson(getTop10Students().getBestStudents())
                    .coin(studentInfo.getCoin())
                    .countModules(totalCountModules)
                    .countLessons(countLessonsList)
                    .currentLesson(studentInfo.getLesson().getNumber())
                    .isLessonOver(studentInfo.getIsLessonOver())
                    .totalScore(studentInfo.getTotalScore())
                    .build();
        } else {
            throw new DataNotFoundException("user not found with this id: " + userId);
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

    public GetStudentFullInfoResponse getStudentFullInfoResponse(String phoneNumber) {
        UserEntity user = userService.getUserByPhoneNumber(phoneNumber);
        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findStudentInfoByStudentId(user.getId());
        StudentInfo studentInfo = studentInfoOptional.get();

        GetStudentFullInfoResponse student = GetStudentFullInfoResponse.builder()

                .avatar(studentInfo.getAvatar())
                .birthday(studentInfo.getBirthday())
                .coin(studentInfo.getCoin())
                .currentLesson(studentInfo.getLesson().getNumber())
                .currentModule(studentInfo.getCurrentModule().getNumber())
                .isLessonOver(false)
                .name(user.getName())
                .surname(user.getSurname())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .score(studentInfo.getTotalScore())
                .subject(studentInfo.getSubject().getTitle())
                .build();
        student.setDiscountList(modelMapper.map(discountRepository.findDiscountEntitiesByStudentId(user.getId()), new TypeToken<List<DiscountResponse>>(){}.getType()));

        List<SpecialAttendanceResponse> attendanceResponseList = new ArrayList<>();
        for (AttendanceEntity attendanceEntity : attendanceRepository.findAllByStudentId(studentInfo.getId())) {
            SpecialAttendanceResponse attendanceResponse = SpecialAttendanceResponse.builder()
                    .moduleNumber(attendanceEntity.getStudent().getCurrentModule().getNumber())
                    .lessonNumber(attendanceEntity.getLessonEntity().getNumber())
                    .score(attendanceEntity.getScore())
                    .build();
            attendanceResponseList.add(attendanceResponse);
        }
        student.setAttendanceList(attendanceResponseList);
        return student;
    }

}
