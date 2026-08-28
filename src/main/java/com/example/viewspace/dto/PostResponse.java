package com.example.viewspace.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

	private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String createdBy;
    private LocalDateTime createdAt;

}
