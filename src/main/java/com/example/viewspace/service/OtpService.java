package com.example.viewspace.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.viewspace.entity.OtpCode;
import com.example.viewspace.repository.OtpCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

	 private final OtpCodeRepository otpCodeRepository;
	    private static final SecureRandom random = new SecureRandom();

	    public String generateAndSaveOtp(String email) {
	        String otp = generateSixDigitOtp();

	        OtpCode otpCode = new OtpCode();
	        otpCode.setEmail(email);
	        otpCode.setOtpCode(otp);
	        otpCode.setExpiryTime(LocalDateTime.now().plusMinutes(5));

	        otpCodeRepository.save(otpCode);
	        return otp;
	    }

	    private String generateSixDigitOtp() {
	        int number = 100000 + random.nextInt(900000); // ensures always 6 digits (100000–999999)
	        return String.valueOf(number);
	    }

}
