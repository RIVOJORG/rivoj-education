package uz.rivoj.education.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.entity.UserRole;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findUserEntityByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);


    Page<UserEntity> findAllByRole(UserRole userRole, Pageable pageable);
}

