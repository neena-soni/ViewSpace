package com.example.viewspace.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.viewspace.repository.OtpCodeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchedulerService {

	 private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

	    private final OtpCodeRepository otpCodeRepository;

	    @Scheduled(fixedRate = 10000) // runs every 10 seconds.
	    @Transactional
	    public void RemoveExpiredOtps() {
	        logger.info("Running scheduled cleanup: purging expired OTPs...");
	        otpCodeRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
	        logger.info("Expired OTP cleanup complete.");
	    }	

}
