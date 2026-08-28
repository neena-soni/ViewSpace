package com.example.viewspace.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.viewspace.dto.PostRequest;
import com.example.viewspace.dto.PostResponse;
import com.example.viewspace.service.PostService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/viewspace/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> createPost(@Valid @RequestPart("postData") PostRequest postData,
                                         @RequestPart("file") MultipartFile file) {
        try {
            
            PostResponse saved = postService.createPost(postData, file);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image. Please try again.");
        }
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updatePost(@PathVariable Long id,
    		@Valid @RequestPart("postData") PostRequest postData,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            PostResponse updated = postService.updatePost(id, postData, file);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update post. Please try again.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}