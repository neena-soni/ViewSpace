package com.example.viewspace.service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.viewspace.config.JwtUtils;
import com.example.viewspace.dto.ForgotPasswordRequest;
import com.example.viewspace.dto.JwtResponse;
import com.example.viewspace.dto.LoginRequest;
import com.example.viewspace.dto.RegisterRequest;
import com.example.viewspace.dto.ResendOtpRequest;
import com.example.viewspace.dto.ResetPasswordRequest;
import com.example.viewspace.dto.VerifyOtpRequest;
import com.example.viewspace.entity.OtpCode;
import com.example.viewspace.entity.UserEntity;
import com.example.viewspace.repository.OtpCodeRepository;
import com.example.viewspace.repository.UserEntityRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserEntityRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final OtpService otpService;
    private final EmailService emailService;
	private final OtpCodeRepository otpCodeRepository;
	  private final JwtUtils jwtUtils;
	  private final AuthenticationManager authenticationManager;
	
    
	public void registerUser(RegisterRequest registerRequest) //throws MessagingException
	{
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        
        UserEntity ue = new UserEntity();
        ue.setUsername(registerRequest.getUsername());
        ue.setEmail(registerRequest.getEmail());
        ue.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        //following fields have defult value set
        //role: ROLE_USER
        //isEnabled: false
        //createdAt: localDateTime.now()
        
        
        userRepository.save(ue);
        
        //String otp = otpService.generateAndSaveOtp(registerRequest.getEmail());
        //emailService.sendOtpEmail(registerRequest.getEmail(),otp);
        
    }

	
	
	
	 public JwtResponse authenticateUser(LoginRequest loginRequest) 
	 {
          Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
           );
  	
   

       SecurityContextHolder.getContext().setAuthentication(authentication);
       String jwt = jwtUtils.generateJwtToken(authentication);

       UserDetails userDetails = (UserDetails) authentication.getPrincipal();
       List<String> roles = userDetails.getAuthorities().stream()
               .map(GrantedAuthority::getAuthority)
               .collect(Collectors.toList());

       
       //Does a fresh DB lookup via userRepository.findByUsername(...) to get the full UserEntity (for email, since UserDetails alone doesn't carry it — this is the trade-off we discussed earlier).
       UserEntity user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(()-> new IllegalArgumentException("User not found."));

       return JwtResponse.builder()
               .token(jwt)
               .type("Bearer")
               .username(user.getUsername())
               .email(user.getEmail())
               .roles(roles)
               .build();
   }
	
	 
	 
	 

		@Transactional
		public void verifyUser(VerifyOtpRequest request)
		{
			//first comparing incoming otp and email with the one present in database.
			OtpCode otpCode = otpCodeRepository.findByEmailAndOtpCode(request.email(),request.otp()).orElseThrow(()->new IllegalArgumentException("Invalid Otp."));
			//checking expiry time.
			if(otpCode.getExpiryTime().isBefore(LocalDateTime.now()))
			{
				throw new IllegalArgumentException("OTP has expired.");  //request new otp.
			}
			
		
			UserEntity user = userRepository.findByEmail(request.email()).orElseThrow(()-> new IllegalArgumentException("User not found."));
			//making user verified and saving.
			//user.setEnabled(true);
			userRepository.save(user);
			
			//deleting otp code record.
			otpCodeRepository.deleteByEmail(request.email());
		}
		
	 
	 
	 
	 
	 
	 @Transactional
	 public void resendOtp(ResendOtpRequest request) throws MessagingException {

	     UserEntity user = userRepository.findByEmail(request.getEmail())
	             .orElseThrow(() -> new IllegalArgumentException("No account found with this email."));

//	     if (user.isEnabled()) {
//	         throw new IllegalArgumentException("Account already verified. Please log in.");
//	     }

	     // clear any existing OTP for this email before generating a new one
	     otpCodeRepository.deleteByEmail(request.getEmail());

	     String otp = otpService.generateAndSaveOtp(request.getEmail());
	     emailService.sendOtpEmail(request.getEmail(), otp);
	 }
	 
	 
	 
	 public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {

		    UserEntity user = userRepository.findByEmail(request.getEmail())
		            .orElseThrow(() -> new IllegalArgumentException("No account found with this email."));
		    // clear any existing OTP for this email before generating a new one
		    otpCodeRepository.deleteByEmail(request.getEmail());

		    String otp = otpService.generateAndSaveOtp(request.getEmail());
		    emailService.sendOtpEmail(request.getEmail(), otp);
		}

	 
	 @Transactional
	 public void resetPassword(ResetPasswordRequest request) 
	 {
		 UserEntity user = userRepository.findByEmail(request.getEmail())
		            .orElseThrow(() -> new IllegalArgumentException("User not found."));
		 
		 OtpCode otpCode = otpCodeRepository.findByEmailAndOtpCode(request.getEmail(), request.getOtp())
		            .orElseThrow(() -> new IllegalArgumentException("Invalid OTP."));

		    if (otpCode.getExpiryTime().isBefore(LocalDateTime.now())) {
		        throw new IllegalArgumentException("OTP has expired. Please request a new one.");
		    }

		    
		    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		    userRepository.save(user);

		    otpCodeRepository.deleteByEmail(request.getEmail());
		}
	 
}
	






































































//WHAT IF user keep clicking on resend otp?

// KNOWN LIMITATION: no rate limiting on this endpoint.
// A user (or bot) can currently call this repeatedly with no restriction,
// which could spam the recipient's inbox and burn through our email
// provider's sending quota (e.g. Gmail SMTP's daily limit).
//
// A simple app-level cooldown check would look like this:
//
// Optional<OtpCode> lastOtp = otpCodeRepository.findTopByEmailOrderByIdDesc(request.getEmail());
// if (lastOtp.isPresent()) {
//     LocalDateTime lastCreatedAt = lastOtp.get().getExpiryTime().minusMinutes(5);
//     if (lastCreatedAt.plusSeconds(60).isAfter(LocalDateTime.now())) {
//         throw new IllegalArgumentException("Please wait a minute before requesting another OTP.");
//     }
// }
//
// REAL-WORLD SOLUTION: production systems don't usually solve this with
// manual DB timestamp checks like above. Instead, they use dedicated
// rate-limiting mechanisms — e.g. Redis-backed rate limiters (fixed window /
// sliding window / token bucket algorithms), or rate limiting at the API
// gateway / reverse proxy layer (Nginx, Cloudflare, AWS API Gateway), or a
// library like Bucket4j / Resilience4j. These are faster, centralized, and
// don't add extra load to the main database for something this frequent.
// Revisit this as a future improvement once the core app is complete.








