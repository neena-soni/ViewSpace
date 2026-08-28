package com.example.viewspace.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    private String publicId; // Cloudinary asset id, needed for cleanup on delete/update

    @Column(nullable = false)
    private String createdBy; // username of the post's owner

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}