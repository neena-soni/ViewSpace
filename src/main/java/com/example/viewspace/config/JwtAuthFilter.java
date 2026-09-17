package com.example.viewspace.config;



import com.example.viewspace.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.example.viewspace.repository.UserEntityRepository;
import com.example.viewspace.service.UserVersionService;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final UserVersionService userVersionService;
    private final UserEntityRepository userEntityRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("Auth header: " + authHeader);
        
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // no token — let it pass through, authorization rules decide
            return;
        }

        String token = authHeader.substring(7); // strip "Bearer " prefix
        
        System.out.println("Extracted token: " + token);
        
        
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUsernameFromJwtToken(token);
            Integer tokenVersion = jwtUtils.getJwtVersionFromJwtToken(token);
            Integer activeVersion = userVersionService.getJwtVersion(username, userEntityRepository);

            // Verify that the token version matches the active version in memory/DB
            if (tokenVersion != null && tokenVersion.equals(activeVersion)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("JWT version mismatch for user " + username + ": token version=" + tokenVersion + ", active version=" + activeVersion);
            }
        }

        filterChain.doFilter(request, response); // continue to next filter / controller
    }
}