package uz.rivoj.education.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.response.BestStudentResponse;
import uz.rivoj.education.dto.response.DiscountResponse;
import uz.rivoj.education.dto.response.HomePageResponse;
import uz.rivoj.education.dto.response.RankingPageResponse;
import uz.rivoj.education.entity.AttendanceEntity;
import uz.rivoj.education.entity.StudentInfo;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.entity.enums.AttendanceStatus;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final StudentInfoRepository studentInfoRepository;
    private final DiscountRepository discountRepository;
    private final AttendanceRepository attendanceRepository;

    public HomePageResponse getProgressByPhoneNumber(String phoneNumber){

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
            if(attendance.getStatus() == AttendanceStatus.CHECKED){
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

    public RankingPageResponse getRanking() {
        //Sort sortByTotalScoreDesc = Sort.by(Sort.Direction.DESC, "totalScore");
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "totalScore"));
        Page<StudentInfo> page = studentInfoRepository.findAll(pageRequest);
        List<StudentInfo> sortedStudents = page.getContent();

        List<BestStudentResponse> bestStudentResponseList = new ArrayList<>();

        for (StudentInfo studentInfo : sortedStudents) {
            Optional<StudentInfo> studentInfoByStudentId = studentInfoRepository.findStudentInfoByStudentId(studentInfo.getId());
            UserEntity user = studentInfoByStudentId.get().getStudent();
            BestStudentResponse bestStudent = BestStudentResponse.builder()
                    .avatar(studentInfo.getAvatar())
                    .name(user.getName())
                    .percentage(studentInfo.getTotalScore())
                    .surname(user.getSurname())
                    .build();
            bestStudentResponseList.add(bestStudent);
        }
        return RankingPageResponse.builder()
                .bestStudents(bestStudentResponseList)
                .build();
    }



}
