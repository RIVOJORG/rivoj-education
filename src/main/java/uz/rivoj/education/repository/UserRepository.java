package uz.rivoj.education.repository;


import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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

    List<UserEntity> findAllByRole(UserRole role);

    @Query("SELECT u FROM users u WHERE u.role = :role AND " +
            "(COALESCE(:searchTerm, '') = '' OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "u.phoneNumber LIKE CONCAT('%', :searchTerm, '%'))")
    Page<UserEntity> findAllByRoleAndSearchTerm(@Param("role") UserRole role,
                                                @Param("searchTerm") String searchTerm,
                                                Pageable pageable);


}

