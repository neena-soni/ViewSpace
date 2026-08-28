package com.example.viewspace.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class PostRequest {

	@NotBlank(message = "Title is required")

	 @Size(max = 150, message = "Title must be under 150 characters")
    private String title;

    @Size(max = 2000,message = "Description must be under 2000 characters")
    private String description;
}
