package com.example.viewspace.entity;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity 
{
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(unique = true, nullable = false, length = 50)
	    private String username;

	    @Column(unique = true, nullable = false, length = 100)
	    private String email;

	    @Column(nullable = false)
	    private String password;

	    //@Column(nullable = false)
	    //private boolean isEnabled = false;

	    @Column(nullable = false)
	    private LocalDateTime createdAt = LocalDateTime.now();

	   
	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private Role role = Role.ROLE_USER;


}
