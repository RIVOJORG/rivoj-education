package uz.rivoj.education.service;

//package uz.rivoj.education.service;
//
//
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.TypeToken;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import uz.rivoj.education.dto.response.*;
//import uz.rivoj.education.entity.*;
//import uz.rivoj.education.entity.enums.AttendanceStatus;
//import uz.rivoj.education.exception.DataNotFoundException;
//import uz.rivoj.education.repository.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
public class ProgressService {
//    private final UserRepository userRepository;
//    private final ModelMapper modelMapper;
//    private final StudentInfoRepository studentInfoRepository;
//    private final DiscountRepository discountRepository;
//    private final AttendanceRepository attendanceRepository;
//    private final CommentRepository commentRepository;
//    private final TeacherInfoRepository teacherInfoRepository;
//    private final LessonRepository lessonRepository;
//    private final ModuleRepository moduleRepository;
//    private final UserService userService;
//
//    public HomePageResponse getProgressByPhoneNumber(String phoneNumber) {
//        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
//                .orElseThrow(() -> new DataNotFoundException("User not found"));
//
//        StudentInfo studentInfo = studentInfoRepository.findByStudentId(userEntity.getId())
//                .orElseThrow(() -> new DataNotFoundException("Student info not found"));
//
//        ModuleEntity currentModule = studentInfo.getCurrentModule();
//        if (currentModule == null) {
//            throw new DataNotFoundException("Current module not found for student");
//        }
//
//        Optional<List<AttendanceEntity>> attendanceList = attendanceRepository.findByStudent_IdAndLesson_Module_Id(userEntity.getId(), currentModule.getId());
//
//        if(attendanceList.isEmpty()){
//            throw new DataNotFoundException("Attendances not found for student");
//        }
//        List<ScoreByAttendance> scores = new ArrayList<>();
//        for (AttendanceEntity attendance : attendanceList.get()) {
//            if (attendance.getStatus() == AttendanceStatus.CHECKED) {
//                scores.add(new ScoreByAttendance(
//                        attendance.getLesson().getNumber(),
//                        attendance.getScore()
//                ));
//            }
//        }
//
//        // Fetch and map discount data
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
//
//
//    public RankingPageResponse getTop10Students() {
//        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "totalScore"));
//        Page<StudentInfo> page = studentInfoRepository.findAll(pageRequest);
//        List<StudentInfo> sortedStudents = page.getContent();
//
//        return mapToBestStudentResponse(sortedStudents);
//    }
//
//    public RankingPageResponse getTop10StudentsBySubject(UUID userId) {
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new DataNotFoundException("User not found"));
//
//        Optional<StudentInfo> studentInfo = studentInfoRepository.findByStudentId(user.getId());
//        if (studentInfo.isEmpty()) {
//            throw new DataNotFoundException("Student info not found!");
//        }
//        List<StudentInfo> list = studentInfoRepository.findTop10BySubject_idOrderByTotalScoreDesc(studentInfo.get().getSubject().getId(), PageRequest.of(0, 10));
//        return mapToBestStudentResponse(list);
//    }
//
//    private RankingPageResponse mapToBestStudentResponse(List<StudentInfo> list) {
//        List<BestStudentResponse> bestStudentResponseList = new ArrayList<>();
//
//        for (StudentInfo s : list) {
//            UserEntity user = s.getStudent();
//            BestStudentResponse bestStudent = BestStudentResponse.builder()
//                    .avatar(s.getAvatar())
//                    .name(user.getName())
//                    .percentage(s.getTotalScore())
//                    .surname(user.getSurname())
//                    .build();
//            bestStudentResponseList.add(bestStudent);
//        }
//        return RankingPageResponse.builder()
//                .bestStudents(bestStudentResponseList).build();
//    }
//
//
//
//
//    private int countCompletedLessons(List<LessonEntity> lessonsForModule, StudentInfo studentInfo) {
//        int count = 0;
//        for (LessonEntity lesson : lessonsForModule) {
//            if (lesson.getNumber() <= studentInfo.getLesson().getNumber()) {
//                count++;
//            }
//        }
//        return count;
//    }
//
//    public GetStudentFullInfoResponse getStudentFullInfoResponse(String phoneNumber) {
//        UserEntity user = userService.getUserByPhoneNumber(phoneNumber);
//        Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findByStudentId(user.getId());
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
//        Optional<List<AttendanceEntity>> attendanceEntities = attendanceRepository.findByStudentId(studentInfo.getId());
//        if(attendanceEntities.isPresent()){
//            for (AttendanceEntity attendanceEntity : attendanceEntities.get()) {
//                SpecialAttendanceResponse attendanceResponse = SpecialAttendanceResponse.builder()
//                        .moduleNumber(attendanceEntity.getStudent().getCurrentModule().getNumber())
//                        .lessonNumber(attendanceEntity.getLesson().getNumber())
//                        .score(attendanceEntity.getScore())
//                        .build();
//                attendanceResponseList.add(attendanceResponse);
//            }
//            student.setAttendanceList(attendanceResponseList);
//        }
//
//        return student;
//    }
//
}
