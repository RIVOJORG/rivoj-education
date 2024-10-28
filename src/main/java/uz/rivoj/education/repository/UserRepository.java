package uz.rivoj.education.repository;


import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.dto.response.TeacherDTO;
import uz.rivoj.education.dto.response.UserDetailsDTO;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);


    Page<UserEntity> findAllByRole(UserRole userRole, Pageable pageable);

    boolean existsByPhoneNumber(String phoneNumber);


    @Query("SELECT u FROM users u WHERE u.role = :role AND " +
            "(COALESCE(:searchTerm, '') = '' OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "u.phoneNumber LIKE CONCAT('%', :searchTerm, '%'))")
    Page<UserEntity> findAllByRoleAndSearchTerm(@Param("role") UserRole role,
                                                @Param("searchTerm") String searchTerm,
                                                Pageable pageable);

    @Query("SELECT new uz.rivoj.education.dto.response.TeacherDTO(u.name, u.surname, u.id) FROM users u WHERE u.role = :role")
    List<TeacherDTO> findTeachers(@Param("role") UserRole role);


    @Query("SELECT new uz.rivoj.education.dto.response.UserDetailsDTO(u.phoneNumber, u.avatar, u.name, u.surname) " +
            "FROM users u " +
            "JOIN teacher_info t ON t.teacher.id = u.id " +
            "WHERE u.role = :role AND t.subject.id = :subjectId")
    Page<UserDetailsDTO> findTeachersByRoleAndSubjectId(UserRole role, UUID subjectId, Pageable pageable);

    @Query("SELECT new uz.rivoj.education.dto.response.UserDetailsDTO(u.phoneNumber, u.avatar, u.name, u.surname) " +
            "FROM users u " +
            "JOIN student_info s ON s.student.id = u.id " +
            "WHERE u.role = :role AND s.subject.id = :subjectId")
    Page<UserDetailsDTO> findStudentsByRoleAndSubjectId(UserRole role, UUID subjectId, Pageable pageable);

    @Query("SELECT new uz.rivoj.education.dto.response.UserDetailsDTO(u.phoneNumber, u.avatar, u.name, u.surname) " +
            "FROM users u " +
            "WHERE u.role = :role")
    Page<UserDetailsDTO> findByRole(UserRole role, Pageable pageable);
}

