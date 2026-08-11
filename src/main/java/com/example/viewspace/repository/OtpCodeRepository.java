package com.example.viewspace.repository;

import com.example.viewspace.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findByEmailAndOtpCode(String email, String otpCode);

    

    void deleteByEmail(String email);

    List<OtpCode> findByExpiryTimeBefore(LocalDateTime time);
    
    void deleteByExpiryTimeBefore(LocalDateTime time);
}