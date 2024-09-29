package uz.rivoj.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.rivoj.education.entity.VerificationCode;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<VerificationCode, String> {
    Optional<VerificationCode> findByPhoneNumber(String phoneNumber);
    void deleteByPhoneNumber(String phoneNumber);
    Optional<VerificationCode> findByCode(Integer code);
}
