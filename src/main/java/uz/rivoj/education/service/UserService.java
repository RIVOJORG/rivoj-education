package uz.rivoj.education.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.AdminCR;
import uz.rivoj.education.dto.request.AuthDto;
import uz.rivoj.education.dto.request.UserCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.entity.*;
import uz.rivoj.education.entity.enums.UserStatus;
import uz.rivoj.education.exception.DataAlreadyExistsException;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.*;
import uz.rivoj.education.service.jwt.JwtUtil;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StudentInfoRepository studentInfoRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TeacherInfoRepository teacherInfoRepository;
    private final AttendanceRepository attendanceRepository;
    private final UploadService uploadService;
    private final LessonRepository lessonRepository;
    private final CommentRepository commentRepository;


    public String add(UserCR dto) {
        Optional<UserEntity> userEntity = userRepository.findByPhoneNumber(dto.getPhoneNumber());
        if (userEntity.isPresent()) {
            throw new DataAlreadyExistsException("User already exists");
        }
        UserEntity map = modelMapper.map(dto, UserEntity.class);
        map.setPassword(passwordEncoder.encode(map.getPassword()));
        userRepository.save(map);
        return "Successfully signed up";
    }

    public JwtResponse signIn(AuthDto dto) {
        UserEntity user = userRepository.findByPhoneNumber(dto.getPhoneNumber())
                .orElseThrow(() -> new DataNotFoundException("user not found"));
      if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return new JwtResponse(jwtUtil.generateToken(user),user.getRole());
        }
        throw new AuthenticationCredentialsNotFoundException("password didn't match");
    }

    public String addAdmin(UserCR userDto) {
        if(userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()) {
            throw  new DataAlreadyExistsException("User already exists");
        }
        UserEntity user = UserEntity.builder()
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .phoneNumber(userDto.getPhoneNumber())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(UserRole.ADMIN)
                .userStatus(UserStatus.UNBLOCK)
                .build();
        UserResponse userResponse = modelMapper.map(userRepository.save(user), UserResponse.class);
        userResponse.setId(user.getId());
        return "Created";
    }

    public List<UserResponse> getAll() {
        List<UserResponse> list = new ArrayList<>();
        for (UserEntity user : userRepository.findAll()) {
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            userResponse.setId(user.getId());
            list.add(userResponse);
        }
        return list;
    }



    public String changePhoneNumber(UUID userId, String newPhoneNumber) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("user not found"));
        if (userRepository.existsByPhoneNumber(newPhoneNumber)){
            throw new DataAlreadyExistsException("phone number already exists");}
        user.setPhoneNumber(newPhoneNumber);
        userRepository.save(user);
        return "Phone number successfully updated for user: " + user.getName();
    }
    public String changePassword(UUID userId,String newPassword) {
        Optional<UserEntity> user = userRepository.findById(userId);
        user.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user.get());
        return "Password number successfully updated for user: " + user.get().getName();
    }

    public String blockUnblockUser(UUID userId, UserStatus status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("user not found"));
        user.setUserStatus(status);
        userRepository.save(user);
        return "Successfully " + status.toString() + "ED";
    }

    public String updateUser(String userPhoneNumber, UserRole userRole) {
        UserEntity user = userRepository.findByPhoneNumber(userPhoneNumber).orElseThrow(
                () -> new DataNotFoundException("User not found"));
        user.setRole(userRole);
        userRepository.save(user);
        return "Successfully updated";
    }

    public Object getUserDetails(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        if(user.getRole().equals(UserRole.ADMIN)){
            return modelMapper.map(user, AdminResponse.class);
        } else if (user.getRole().equals(UserRole.TEACHER)) {
            TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(userId)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
            TeacherResponse teacherResponse = modelMapper.map(user, TeacherResponse.class);
            teacherResponse.setSubject(SubjectResponse.builder().title(teacherInfo.getSubject().getTitle()).id(teacherInfo.getSubject().getId()).build());
            teacherResponse.setAbout(teacherInfo.getAbout());
            teacherResponse.setId(teacherInfo.getId());
            teacherResponse.setBirthday(teacherInfo.getTeacher().getBirthday());
            return teacherResponse;
        } else {
            StudentInfo studentInfo = studentInfoRepository.findByStudentId(userId)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
            StudentResponse studentResponse = modelMapper.map(user, StudentResponse.class);
            studentResponse.setBirth(studentInfo.getStudent().getBirthday());
            studentResponse.setSubjectId(studentInfo.getSubject().getId());
            studentResponse.setCurrentLessonId(studentInfo.getLesson().getId());
            studentResponse.setId(studentInfo.getId());
            studentResponse.setCurrentModuleId(studentInfo.getCurrentModule().getId());
            studentResponse.setCurrentLessonNumber(studentInfo.getLesson().getNumber());
            studentResponse.setCurrentModuleNumber(studentInfo.getCurrentModule().getNumber());
            studentResponse.setTotalCoins(studentInfo.getCoin());
            studentResponse.setTotalScore(studentInfo.getTotalScore());
            return studentResponse;
        }

    }

    public Map<String, Object> getAllByRole(UserRole role, String searchTerm, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        Page<UserEntity> userPage;
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            userPage = userRepository.findAllByRole(role, pageable);
        } else {
            userPage = userRepository.findAllByRoleAndSearchTerm(role, searchTerm, pageable);
        }
        List<?> responseList;
        if (role.equals(UserRole.TEACHER)) {
            List<TeacherResponse> teacherResponseList = new ArrayList<>();
            userPage.getContent().forEach(teacherEntity -> {
                TeacherResponse teacherResponse = modelMapper.map(teacherEntity, TeacherResponse.class);
                TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherEntity.getId())
                        .orElseThrow(() -> new DataNotFoundException("Teacher not found " + teacherEntity.getId()));
                if(teacherInfo.getSubject() != null){
                    SubjectResponse subjectResponse = new SubjectResponse(teacherInfo.getSubject().getTitle(), teacherInfo.getSubject().getId());
                    teacherResponse.setSubject(subjectResponse);
                }
                teacherResponse.setBirthday(teacherInfo.getTeacher().getBirthday());
                teacherResponse.setAbout(teacherInfo.getAbout());
                teacherResponse.setStatus(teacherEntity.getUserStatus());
                teacherResponse.setPhoneNumber(teacherEntity.getPhoneNumber());
                teacherResponseList.add(teacherResponse);
            });
            responseList = teacherResponseList;
        } else if (role.equals(UserRole.STUDENT)) {
            List<StudentResponse> studentResponseList = new ArrayList<>();
            userPage.getContent().forEach(student -> {
                StudentInfo studentInfo = studentInfoRepository.findByStudentId(student.getId())
                        .orElseThrow(() -> new DataNotFoundException("User not found"));
                StudentResponse studentResponse = modelMapper.map(student, StudentResponse.class);
                studentResponse.setBirth(studentInfo.getStudent().getBirthday());
                studentResponse.setCurrentLessonId(studentInfo.getLesson() != null ? studentInfo.getLesson().getId() : null);
                studentResponse.setCurrentModuleId(studentInfo.getCurrentModule() != null ? studentInfo.getCurrentModule().getId() : null);
                studentResponse.setSubjectId(studentInfo.getSubject() != null ? studentInfo.getSubject().getId() : null);
                studentResponse.setCurrentLessonNumber(studentInfo.getLesson() != null ? studentInfo.getLesson().getNumber() : null);
                studentResponse.setCurrentModuleNumber(studentInfo.getCurrentModule() != null ? studentInfo.getCurrentModule().getNumber() : null);
                studentResponse.setSubjectName(studentInfo.getSubject() != null ? studentInfo.getSubject().getTitle() : null);
                studentResponse.setTotalCoins(studentInfo.getCoin());
                studentResponse.setTotalScore(studentInfo.getTotalScore());
                studentResponse.setStatus(studentInfo.getStudent().getUserStatus());
                studentResponseList.add(studentResponse);
            });
            responseList = studentResponseList;
        } else {
            List<UserEntity> adminEntityList = userPage.getContent();
            TypeToken<List<AdminResponse>> typeToken = new TypeToken<>() {};
            responseList = modelMapper.map(adminEntityList, typeToken.getType());
        }

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("pageNumber", userPage.getNumber() + 1);
        responseMap.put("totalPages", userPage.getTotalPages());
        responseMap.put("totalCount", userPage.getTotalElements());
        responseMap.put("pageSize", userPage.getSize());
        responseMap.put("hasPreviousPage", userPage.hasPrevious());
        responseMap.put("hasNextPage", userPage.hasNext());
        responseMap.put("data", responseList);

        return responseMap;
    }

    public List<TeacherDTO> getTeachers() {
        return userRepository.findTeachers(UserRole.TEACHER);
    }





    public Map<String, Object> getUsersByRoleAndSubjectId(UserRole role, UUID subjectId, Pageable pageable) {
        Page<UserDetailsDTO> userPage;

        if (role == UserRole.TEACHER) {
            userPage = userRepository.findTeachersByRoleAndSubjectId(role, subjectId, pageable);
        } else if (role == UserRole.STUDENT) {
            userPage = userRepository.findStudentsByRoleAndSubjectId(role, subjectId, pageable);
        } else if (role == UserRole.ADMIN) {
            userPage = userRepository.findByRole(role, pageable);
        } else {
            throw new IllegalArgumentException("Invalid role type");
        }

        List<UserDetailsDTO> responseList = userPage.getContent();
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("pageNumber", userPage.getNumber() + 1);
        responseMap.put("totalPages", userPage.getTotalPages());
        responseMap.put("totalCount", userPage.getTotalElements());
        responseMap.put("pageSize", userPage.getSize());
        responseMap.put("hasPreviousPage", userPage.hasPrevious());
        responseMap.put("hasNextPage", userPage.hasNext());
        responseMap.put("data", responseList);
        return responseMap;
    }

    @SneakyThrows
    public String updateProfilePicture(MultipartFile picture, UUID userId)  {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("user not found!"));
        if(userEntity.getAvatar() != null){
            uploadService.deleteFile(userEntity.getAvatar());
        }
        String filename = userEntity.getName() + "_ProfilePicture";
        String avatarPath = uploadService.uploadFile(picture, filename);
        userEntity.setAvatar(avatarPath);
        userRepository.save(userEntity);
        return "Profile picture changed";
    }

    public String updateProfile(AdminCR adminUpdate, UUID userId) {
        UserEntity admin = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("user not found!"));
        if(adminUpdate.getName() != null){
            admin.setName(adminUpdate.getName());
        }if(adminUpdate.getPassword() != null){
            admin.setPassword(passwordEncoder.encode(adminUpdate.getPassword()));
        }if(adminUpdate.getSurname() != null){
            admin.setSurname(adminUpdate.getSurname());
        }if(adminUpdate.getPhoneNumber() != null){
            admin.setPhoneNumber(adminUpdate.getPhoneNumber());
        }
        userRepository.save(admin);
        return "Profile successfully changed!";
    }

    @Transactional
    public String deleteUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserRole role = user.getRole();

        switch (role) {
            case ADMIN:
                userRepository.deleteById(userId);
                break;

            case TEACHER:

                TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(user.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Teacher info not found for user id: " + userId));

                Optional<List<AttendanceEntity>> attendanceEntityList = attendanceRepository.findAllByTeacherId(teacherInfo.getId());
                attendanceEntityList.ifPresent(attendanceEntities -> attendanceEntities.forEach(
                        attendanceEntity -> {
                            attendanceEntity.setTeacher(null);
                            attendanceRepository.save(attendanceEntity);
                        }
                ));

                Optional<List<LessonEntity>> lessons = lessonRepository.findByTeacherInfoId(teacherInfo.getId());
                if(lessons.isPresent()){
                    for (LessonEntity lesson : lessons.get()) {
                        lesson.setTeacherInfo(null);
                        lessonRepository.save(lesson);
                    }
                }


                Optional<List<CommentEntity>> comments = commentRepository.findByOwnerId(user.getId());
                comments.ifPresent(commentRepository::deleteAll);
                uploadService.deleteFile(user.getAvatar());
                teacherInfoRepository.delete(teacherInfo);
                userRepository.delete(user);
                break;

            case STUDENT:
                StudentInfo studentInfo = studentInfoRepository.findByStudentId(user.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Student info not found for user id: " + userId));
                Optional<List<AttendanceEntity>> attendances = attendanceRepository.findByStudentId(studentInfo.getId());
                attendances.ifPresent(attendanceEntities -> attendanceEntities.forEach(
                        attendanceEntity -> {
                            attendanceEntity.getAnswers().forEach(uploadService::deleteFile);
                            attendanceRepository.delete(attendanceEntity);
                        }
                ));
                Optional<List<CommentEntity>> studentComments = commentRepository.findByOwnerId(user.getId());
                studentComments.ifPresent(commentRepository::deleteAll);
                uploadService.deleteFile(user.getAvatar());
                studentInfoRepository.delete(studentInfo);
                userRepository.delete(user);
                break;

            default:
                throw new IllegalArgumentException("Unsupported role for deletion: " + role);
        }
        return "Deleted!";
    }

    public Map<String, Object> getStudentsBySubject(UUID teacherId, String searchTerm, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        TeacherInfo teacherInfo = teacherInfoRepository.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found!"));
        Page<UserEntity> userPage;
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            userPage = userRepository.findStudentsBySubjectId(UserRole.STUDENT,teacherInfo.getSubject().getId(),pageable);
        } else {
            userPage = userRepository.findStudentsSearchTermAndSubjectId(UserRole.STUDENT, teacherInfo.getSubject().getId(),searchTerm,pageable);
        }
        List<StudentResponse> studentResponseList = new ArrayList<>();
        for (UserEntity student : userPage.getContent()) {
            StudentInfo studentInfo = studentInfoRepository.findByStudentId(student.getId())
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
            StudentResponse studentResponse = modelMapper.map(student, StudentResponse.class);
            studentResponse.setBirth(studentInfo.getStudent().getBirthday());
            studentResponse.setCurrentLessonId(studentInfo.getLesson() != null ? studentInfo.getLesson().getId() : null);
            studentResponse.setCurrentModuleId(studentInfo.getCurrentModule() != null ? studentInfo.getCurrentModule().getId() : null);
            studentResponse.setSubjectId(studentInfo.getSubject() != null ? studentInfo.getSubject().getId() : null);
            studentResponse.setCurrentLessonNumber(studentInfo.getLesson() != null ? studentInfo.getLesson().getNumber() : null);
            studentResponse.setCurrentModuleNumber(studentInfo.getCurrentModule() != null ? studentInfo.getCurrentModule().getNumber() : null);
            studentResponse.setSubjectName(studentInfo.getSubject() != null ? studentInfo.getSubject().getTitle() : null);
            studentResponse.setTotalCoins(studentInfo.getCoin());
            studentResponse.setTotalScore(studentInfo.getTotalScore());
            studentResponse.setStatus(studentInfo.getStudent().getUserStatus());
            studentResponseList.add(studentResponse);
        }
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("pageNumber", userPage.getNumber() + 1);
        responseMap.put("totalPages", userPage.getTotalPages());
        responseMap.put("totalCount", userPage.getTotalElements());
        responseMap.put("pageSize", userPage.getSize());
        responseMap.put("hasPreviousPage", userPage.hasPrevious());
        responseMap.put("hasNextPage", userPage.hasNext());
        responseMap.put("data", studentResponseList);
        return responseMap;
    }
}

