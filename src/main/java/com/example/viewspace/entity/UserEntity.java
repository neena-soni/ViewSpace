package com.example.viewspace.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	   
	    
	    
	    @ElementCollection(fetch = FetchType.EAGER)
	    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	    @Enumerated(EnumType.STRING)
	    @Column(name = "role", nullable = false)
	    private Set<Role> roles = new HashSet<>(Set.of(Role.ROLE_USER));  //default role is user.


}
