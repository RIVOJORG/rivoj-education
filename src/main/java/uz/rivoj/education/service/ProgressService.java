package uz.rivoj.education.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.*;
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

//    public HomePageResponse getProgressByPhoneNumber(String phoneNumber) {
//        // Fetch user entity by phone number
//        UserEntity userEntity = userRepository.findUserEntityByPhoneNumber(phoneNumber)
//                .orElseThrow(() -> new DataNotFoundException("User not found"));
//
//        // Fetch student info by user ID
//        StudentInfo studentInfo = studentInfoRepository.findStudentInfoByStudentId(userEntity.getId())
//                .orElseThrow(() -> new DataNotFoundException("Student info not found"));
//
//        // Fetch the current module for the student
//        ModuleEntity currentModule = studentInfo.getCurrentModule();
//        if (currentModule == null) {
//            throw new DataNotFoundException("Current module not found for student");
//        }
//
//        // Fetch attendance entities for the student in the current module
//        List<AttendanceEntity> attendancesOfModule = attendanceRepository.findAttendanceEntitiesByStudent_IdAndLesson_Module_Id(
//                userEntity.getId(),
//                currentModule.getId()
//        );
//
//        // Prepare list to hold scores
//        List<ScoreByAttendance> scores = new ArrayList<>();
//
//        // Process attendance data to extract scores
//        for (AttendanceEntity attendance : attendancesOfModule) {
//            if (attendance.getStatus() == AttendanceStatus.CHECKED) {
//                scores.add(new ScoreByAttendance(
//                        attendance.getLesson().getNumber(),
//                        attendance.getScore()
//                ));
//            }
//        }

//        List<DiscountResponse> discounts = discountRepository.findDiscountEntitiesByStudentId(userEntity.getId())
//                .stream()
//                .map(discount -> modelMapper.map(discount, DiscountResponse.class))
//                .collect(Collectors.toList());
//
//        // Build and return the HomePageResponse
//        return HomePageResponse.builder()
//                .phoneNumber(userEntity.getPhoneNumber())
//                .name(userEntity.getName())
//                .surname(userEntity.getSurname())
//                .avatar(studentInfo.getAvatar())
//                .currentModule(currentModule.getNumber())
//                .currentLesson(studentInfo.getLesson().getNumber())
//                .isLessonOver(studentInfo.getIsLessonOver())
//                .coin(studentInfo.getCoin())
//                .totalScore(studentInfo.getTotalScore())
//                .scores(scores)
//                .discounts(discounts)
//                .build();
//    }


    @Cacheable(value = "rankings", key = "'top10Students_' + #userId")
    public RankingPageResponse getTop10Students(UUID userId) {
        StudentInfo studentInfo = studentInfoRepository.findByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found"));

        List<StudentInfo> allRankedStudents = studentInfoRepository.findAllByOrderByTotalScoreDesc()
                .orElseThrow(() -> new DataNotFoundException("No students found"));

        int userOrder = -1;
        for (int i = 0; i < allRankedStudents.size(); i++) {
            if (allRankedStudents.get(i).getStudent().getId().equals(userId)) {
                userOrder = i;
                break;
            }
        }
        if (userOrder == -1) throw new DataNotFoundException("User not found in the ranking");

        int userRank = userOrder + 1;

        List<StudentInfo> topStudents = allRankedStudents.stream()
                .limit(10)
                .collect(Collectors.toList());

        if (userRank > 10) topStudents.add(studentInfo);

        RankingPageResponse rankingPageResponse = mapToBestStudentResponse(topStudents);
        rankingPageResponse.setUserOrder(userOrder);
        rankingPageResponse.setUserRank(userRank);

        return rankingPageResponse;
    }


    @Cacheable(value = "rankings", key = "'top10StudentsBySubject_' + #userId")
    public RankingPageResponse getTop10StudentsBySubject(UUID userId) {
        StudentInfo studentInfo = studentInfoRepository.findByStudentId(userId)
                .orElseThrow(() -> new DataNotFoundException("Student not found"));

        List<StudentInfo> allRankedStudents = studentInfoRepository
                .findAllBySubject_idOrderByTotalScoreDesc(studentInfo.getSubject().getId())
                .orElseThrow(() -> new DataNotFoundException("No students found for the subject"));
        int userOrder = -1;
        for (int i = 0; i < allRankedStudents.size(); i++) {
            if (allRankedStudents.get(i).getStudent().getId().equals(userId)) {
                userOrder = i;
                break;
            }
        }
        if (userOrder == -1) throw new DataNotFoundException("User not found in the ranking");

        int userRank = userOrder + 1;
        List<StudentInfo> topStudents = allRankedStudents.stream().limit(10).collect(Collectors.toList());

        if (userRank > 10) topStudents.add(studentInfo);

        RankingPageResponse rankingPageResponse = mapToBestStudentResponse(topStudents);
        rankingPageResponse.setUserOrder(userOrder);
        rankingPageResponse.setUserRank(userRank);

        return rankingPageResponse;
    }

    private RankingPageResponse mapToBestStudentResponse(List<StudentInfo> list) {
        List<BestStudentResponse> bestStudentResponseList = new ArrayList<>();

        for (StudentInfo s : list) {
            UserEntity user = s.getStudent();
            BestStudentResponse bestStudent = BestStudentResponse.builder()
                    .avatar(s.getStudent().getAvatar())
                    .name(user.getName())
                    .percentage(s.getTotalScore())
                    .surname(user.getSurname())
                    .build();
            bestStudentResponseList.add(bestStudent);
        }
        return RankingPageResponse.builder()
                .bestStudents(bestStudentResponseList).build();
    }


//    public LessonPageResponse getLessonPageResponseByLessonId(UUID userId, UUID lessonId) {
//        UserEntity student = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Student not found"));
//
//        StudentInfo studentInfo = studentInfoRepository.findByStudent(student);
//        if (studentInfo == null) {
//            throw new RuntimeException("Student information not found");
//        }
//
//        LessonEntity lesson = lessonRepository.findById(lessonId).orElseThrow(
//                () -> new DataNotFoundException("lesson not found")
//        );
//
//        TeacherInfo teacherInfo = teacherInfoRepository
//                .findTeacherInfoBySubjectId(lesson.getModule().getSubject().getId())
//                .orElseThrow(
//                        () -> new DataNotFoundException("teacher info not found")
//                );
//
//        TeacherResponse teacherResponse = TeacherResponse.builder()
//                .name(teacherInfo.getTeacher().getName())
//                .surname(teacherInfo.getTeacher().getSurname())
//                .avatar(teacherInfo.getAvatar())
//                .subject(modelMapper.map(teacherInfo.getSubject(), SubjectResponse.class))
//                .about(teacherInfo.getAbout())
//                .id(teacherInfo.getId())
//                .build();
//
//        List<CommentResponse> comments = commentRepository.findCommentEntitiesByLesson_Id(lessonId)
//                .stream().map(comment -> modelMapper.map(comment, CommentResponse.class))
//                .toList();
//
//        List<AttendanceResponse> attendances = attendanceRepository
//                .findByStudent_IdAndLesson_Id(studentInfo.getId(), lesson.getId())
//                .stream().map(attendance -> modelMapper.map(attendance, AttendanceResponse.class))
//                .toList();
//
//        return LessonPageResponse.builder()
//                .source(lesson.getSource())
//                .cover(lesson.getCover())
//                .teacher(teacherResponse)
//                .comments(comments)
//                .attendances(attendances).build();
//    }

//    public EducationPageResponse getEducationPage(UUID userId) {
//        UserEntity student = userRepository.findById(UUID.fromString(String.valueOf(userId)))
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        StudentInfo studentInfoEntity = studentInfoRepository.findByStudent(student);
//        if (studentInfoEntity == null) {
//            throw new RuntimeException("Student information not found");
//        }
//        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findStudentInfoByStudentId(studentInfoEntity.getId());
//
//        if (studentInfoOptional.isPresent()) {
//            StudentInfo studentInfo = studentInfoOptional.get();
//
//            int totalCountModules = moduleRepository.countBySubject(studentInfo.getSubject());
//            List<Integer> countLessonsList = new ArrayList<>();
//
//            for (int i = 1; i <= totalCountModules; i++) {
//                List<LessonEntity> lessonsForModule = lessonRepository.findByModuleNumber(i);
//
//                int countLessons = countCompletedLessons(lessonsForModule, studentInfo);
//                countLessonsList.add(countLessons);
//            }
//
//            return EducationPageResponse.builder()
//                    .bestStudentsOfLesson(getTop10Students().getBestStudents())
//                    .coin(studentInfo.getCoin())
//                    .countModules(totalCountModules)
//                    .countLessons(countLessonsList)
//                    .currentLesson(studentInfo.getLesson().getNumber())
//                    .isLessonOver(studentInfo.getIsLessonOver())
//                    .totalScore(studentInfo.getTotalScore())
//                    .build();
//        } else {
//            throw new DataNotFoundException("user not found with this id: " + userId);
//        }
//    }

    private int countCompletedLessons(List<LessonEntity> lessonsForModule, StudentInfo studentInfo) {
        int count = 0;
        for (LessonEntity lesson : lessonsForModule) {
            if (lesson.getNumber() <= studentInfo.getLesson().getNumber()) count++;
        }
        return count;
    }

//    public GetStudentFullInfoResponse getStudentFullInfoResponse(String phoneNumber) {
//        UserEntity user = userService.getUserByPhoneNumber(phoneNumber);
//        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findStudentInfoByStudentId(user.getId());
//        StudentInfo studentInfo = studentInfoOptional.get();
//
//        GetStudentFullInfoResponse student = GetStudentFullInfoResponse.builder()
//
//                .avatar(studentInfo.getAvatar())
//                .birthday(studentInfo.getBirthday())
//                .coin(studentInfo.getCoin())
//                .currentLesson(studentInfo.getLesson().getNumber())
//                .currentModule(studentInfo.getCurrentModule().getNumber())
//                .isLessonOver(false)
//                .name(user.getName())
//                .surname(user.getSurname())
//                .password(user.getPassword())
//                .phoneNumber(user.getPhoneNumber())
//                .score(studentInfo.getTotalScore())
//                .subject(studentInfo.getSubject().getTitle())
//                .build();
//        student.setDiscountList(modelMapper.map(discountRepository.findDiscountEntitiesByStudentId(user.getId()), new TypeToken<List<DiscountResponse>>(){}.getType()));
//
//        List<SpecialAttendanceResponse> attendanceResponseList = new ArrayList<>();
//        for (AttendanceEntity attendanceEntity : attendanceRepository.findAllByStudentId(studentInfo.getId())) {
//            SpecialAttendanceResponse attendanceResponse = SpecialAttendanceResponse.builder()
//                    .moduleNumber(attendanceEntity.getStudent().getCurrentModule().getNumber())
//                    .lessonNumber(attendanceEntity.getLesson().getNumber())
//                    .score(attendanceEntity.getScore())
//                    .build();
//            attendanceResponseList.add(attendanceResponse);
//        }
//        student.setAttendanceList(attendanceResponseList);
//        return student;
//    }

}
