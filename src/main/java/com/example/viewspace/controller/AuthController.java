package com.example.viewspace.controller;

import org.apache.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.viewspace.dto.ForgotPasswordRequest;
import com.example.viewspace.dto.LoginRequest;
import com.example.viewspace.dto.RegisterRequest;
import com.example.viewspace.dto.ResendOtpRequest;
import com.example.viewspace.dto.ResetPasswordRequest;
import com.example.viewspace.dto.VerifyOtpRequest;
import com.example.viewspace.service.AuthService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/viewspace/auth")
@RequiredArgsConstructor
public class AuthController 
{
	private final AuthService authService;
	
	 @PostMapping("/register")
	    public ResponseEntity<?> register( @RequestBody RegisterRequest request) {
	        try {
	            authService.registerUser(request);
//	            return ResponseEntity.ok("OTP sent to your email. Please verify to complete registration.");
	            return ResponseEntity.ok("Registration Successful.");
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
	        } 
//	            catch (MessagingException e) {
//	            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
//	                    .body("Registration succeeded but failed to send OTP email.");
//	        }
	    }
	 
	 
	 @PostMapping("/verify-otp")
	 public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request)
	 {
		 try
		 {
			 authService.verifyUser(request);
			 return ResponseEntity.ok("OTP(Account) verified. Please login.");

		 }catch (IllegalArgumentException e) {
		        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
		    }
	}
	 
	 
	 @PostMapping("/login")
	    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) 
	   {
		 
		 //return ResponseEntity.ok(authService.authenticateUser(loginRequest));

		 try
		 {
			 return ResponseEntity.ok(authService.authenticateUser(loginRequest));
 
		 }catch (org.springframework.security.authentication.DisabledException e) {
		        // Triggers when u.isEnabled() is false in database
		        return ResponseEntity.status(HttpStatus.SC_FORBIDDEN)
		                .body("Account is not verified. Please check your email for the OTP.");
		 }catch(AuthenticationException ae)
		 {
			 return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED)
	                    .body("Authentication failed: Invalid username/password.");
		 }
		 catch(IllegalArgumentException e)
		 {
			 return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
		 }
	   }

	 
	
	 @PostMapping("/resend-otp")
	 public ResponseEntity<?> resendOtp( @RequestBody ResendOtpRequest request) {
	     try {
	         authService.resendOtp(request);
	         return ResponseEntity.ok("A new OTP has been sent to your email.");
	     } catch (IllegalArgumentException e) {
	         return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
	     } catch (MessagingException e) {
	         return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
	                 .body("Failed to send OTP email. Please try again.");
	     }
	 }
	 
	 
	 
	 @PostMapping("/forgot-password")
	 public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
	     try {
	         authService.forgotPassword(request);
	         return ResponseEntity.ok("OTP sent to your email for password reset.");
	     } catch (IllegalArgumentException e) {
	         return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
	     } catch (MessagingException e) {
	         return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
	                 .body("Failed to send OTP email. Please try again.");
	     }
	 }

	 @PostMapping("/reset-password")
	 public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
	     try {
	         authService.resetPassword(request);
	         return ResponseEntity.ok("Password reset successful. Please log in with your new password.");
	     } catch (IllegalArgumentException e) {
	         return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
	     }
	 }
	 
	 
	 
	 
	 
}


















//todo:  using @Valid
