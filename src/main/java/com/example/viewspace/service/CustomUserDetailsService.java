package com.example.viewspace.service;

import java.util.Set;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.viewspace.entity.UserEntity;
import com.example.viewspace.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{


private final UserEntityRepository repo;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserEntity u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
		
		Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(u.getRole().name()));
		
		return new User(u.getUsername(),
				       u.getPassword(),
				       //u.isEnabled(),
				       true, //isEnabled.
				       true,   //accountNotExpired
				       true,  //credentialNotExpired
				       true,   //accountNotLocked.
				       authorities);
		
	}

}
