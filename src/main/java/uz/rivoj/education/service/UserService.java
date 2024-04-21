package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.LoginRequest;
import uz.rivoj.education.dto.request.TeacherInfoRequest;
import uz.rivoj.education.dto.request.UserCreateRequest;
import uz.rivoj.education.dto.response.DiscountResponse;
import uz.rivoj.education.dto.response.HomePageResponse;
import uz.rivoj.education.dto.response.SubjectResponse;
import uz.rivoj.education.dto.response.UserResponse;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.exception.WrongPasswordException;
import uz.rivoj.education.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final ModuleRepository moduleRepository;
    private final DiscountRepository discountRepository;
    private final AttendanceRepository attendanceRepository;
    private final ModelMapper modelMapper;
//    private final PasswordEncoder passwordEncoder;

    public UserResponse add(UserCreateRequest userDto) {
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setRole(UserRole.STUDENT);
//        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return modelMapper.map(userRepository.save(userEntity), UserResponse.class);
    }

    public UserResponse login(LoginRequest login) {
        UserEntity userEntity = userRepository.findUserEntityByPhoneNumber(login.getPhoneNumber())
                .orElseThrow(
                        () -> new DataNotFoundException("user not found")
                );

        System.out.println("login.getPassword() = " + login.getPassword());
        System.out.println("userEntity.getPassword() = " + userEntity.getPassword());
        if(Objects.equals(login.getPassword(), userEntity.getPassword())) {
            return modelMapper.map(userEntity, UserResponse.class);
        }
        throw new WrongPasswordException("password didn't match");
    }

    public TeacherInfo createTeacher(TeacherInfoRequest teacherInfo) {
        TeacherInfo teacher = modelMapper.map(teacherInfo, TeacherInfo.class);
        UserEntity userEntity = userRepository.findById(teacher.getId()).orElseThrow(
                () -> new DataNotFoundException("user not found")
        );
        userEntity.setRole(UserRole.TEACHER);
        userRepository.save(userEntity);
        return teacherInfoRepository.save(teacher);
    }

    public List<UserResponse> getAll() {
        List<UserResponse> list = new ArrayList<>();
        for (UserEntity user : userRepository.findAll()) {
            list.add(modelMapper.map(user, UserResponse.class));
        }
        return list;
    }

    public UserResponse getUser(UUID id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("user not found")
        );
        return modelMapper.map(userEntity, UserResponse.class);
    }


    /** Hali tugatmadim */
    public HomePageResponse myProgressByPhoneNumber(String phoneNumber){

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
            if(attendance.getStatus()){
                scores.add(attendance.getLessonEntity().getNumber(), attendance.getScore());
            }
        }

        List<DiscountResponse> discounts = discountRepository.findDiscountEntitiesByStudentId(userEntity.getId())
                .stream().map(discount -> modelMapper.map(discount, DiscountResponse.class))
                .collect(Collectors.toList());

        HomePageResponse homePageResponse = HomePageResponse.builder()
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
        return null;
    }
}
