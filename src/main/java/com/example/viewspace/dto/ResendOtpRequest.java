package com.example.viewspace.dto;


import lombok.Data;

@Data
public class ResendOtpRequest {

	//TODO: implement validations and exception handling.
    //@NotBlank(message = "Email is required")
    //@Email(message = "Email should be valid")
    private String email;
}
