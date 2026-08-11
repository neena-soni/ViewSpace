package com.example.viewspace.dto;

import lombok.Data;

@Data
public class RegisterRequest {

	//todo: apply validations:  notblank , email,size.
	private String username;
	private String email;
	private String password;
}
