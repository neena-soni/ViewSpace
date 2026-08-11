package com.example.viewspace.service;


import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EmailService {

	
	private final JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmail;
			
//	public void sendMail(String to, String otp)
//	{
//		SimpleMailMessage msg = new SimpleMailMessage();
//		msg.setFrom(from);
//		msg.setTo(to);
//		msg.setSubject("Your Viewspace Verification Code");
//		msg.setText("Your OTP is: " + otp + "\n\nThis code will expire in 5 minutes.");
//		mailSender.send(msg);
//		
//	}
	
	 public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
	        String subject = "Your Viewspace Verification Code";
	        String htmlMessage = buildOtpEmailHtml(otp);

	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart (needed for HTML)
	        helper.setFrom(fromEmail);
	        helper.setTo(toEmail);
	        helper.setSubject(subject);
	        helper.setText(htmlMessage, true); // true = isHtml

	        mailSender.send(message);
	    }
	
	
	 private String buildOtpEmailHtml(String otp) {
	        return "<html>"
	                + "<body style=\"font-family: Arial, sans-serif;\">"
	                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
	                + "<h2 style=\"color: #333;\">Welcome to Viewspace!</h2>"
	                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
	                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
	                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
	                + "<p style=\"font-size: 22px; font-weight: bold; color: #007bff;\">" + otp + "</p>"
	                + "<p style=\"font-size: 13px; color: #888;\">This code expires in 5 minutes.</p>"
	                + "</div>"
	                + "</div>"
	                + "</body>"
	                + "</html>";
	    }
	
}
