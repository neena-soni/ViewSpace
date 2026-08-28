package com.example.viewspace.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.viewspace.dto.PostRequest;
import com.example.viewspace.dto.PostResponse;
import com.example.viewspace.entity.Post;
import com.example.viewspace.repository.PostRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final Cloudinary cloudinary;

    public PostResponse createPost(PostRequest postData, MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Post post = new Post();
        post.setTitle(postData.getTitle());
        post.setDescription(postData.getDescription());
        post.setImageUrl(uploadResult.get("secure_url").toString());
        post.setPublicId(uploadResult.get("public_id").toString());
        post.setCreatedBy(currentUsername);

        Post saved = postRepository.save(post);
        return toResponseDto(saved);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponseDto).toList();
    }

    public PostResponse getPostById(Long id) {
        return toResponseDto(postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id))); 
    }

    public PostResponse updatePost(Long id, PostRequest postData, MultipartFile file) throws IOException {
        Post existing = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        enforceOwnershipOrAdmin(existing);

        existing.setTitle(postData.getTitle());
        existing.setDescription(postData.getDescription());

        if (file != null && !file.isEmpty()) {
            if (existing.getPublicId() != null) {
                cloudinary.uploader().destroy(existing.getPublicId(), ObjectUtils.asMap("invalidate", true));
            }
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            existing.setImageUrl(uploadResult.get("secure_url").toString());
            existing.setPublicId(uploadResult.get("public_id").toString());
        }

        Post updated = postRepository.save(existing);
        return toResponseDto(updated);
    }

    public void deletePost(Long id) {
        Post post =  postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        enforceOwnershipOrAdmin(post);

        if (post.getPublicId() != null) {
            try {
                cloudinary.uploader().destroy(post.getPublicId(), ObjectUtils.asMap("invalidate", true));
            } catch (IOException e) {
                e.printStackTrace(); // a failed Cloudinary cleanup shouldn't block deleting the DB record
            }
        }

        postRepository.deleteById(id);
    }

    /**
     * THE REAL SECURITY FIX: verifies the currently authenticated user either
     * owns this post, or holds ROLE_ADMIN. Throws AccessDeniedException otherwise,
     * which Spring Security automatically converts into a 403 response.
     *
     * 
     */
    private void enforceOwnershipOrAdmin(Post post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean isOwner = post.getCreatedBy() != null && post.getCreatedBy().equals(currentUsername);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to modify this post.");
        }
    }
    
    
    
    private PostResponse toResponseDto(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                post.getCreatedBy(),
                post.getCreatedAt()
        );
    }
}