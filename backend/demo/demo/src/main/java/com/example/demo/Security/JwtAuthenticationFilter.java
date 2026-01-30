package com.example.demo.Security;

import com.example.demo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("===== JWT FILTER HIT =====");
        System.out.println("Path: " + request.getServletPath());

        // Skip auth endpoints
        if (request.getServletPath().startsWith("/auth")) {
            System.out.println("Skipping auth endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            System.out.println("Token extracted");

            boolean valid = jwtUtil.validateToken(token);
            System.out.println("Token valid: " + valid);

            if (valid) {
                String username = jwtUtil.extractUsername(token);
                System.out.println("Username from token: " + username);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {

                    userRepository.findByUsername(username).ifPresentOrElse(user -> {

                        System.out.println("User found in DB: " + user.getUsername());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getUsername(),
                                        null,
                                        List.of()
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);

                        System.out.println("Authentication set in SecurityContext");

                    }, () -> {
                        System.out.println("User NOT found in DB");
                    });
                }
            }
        } else {
            System.out.println("No Bearer token found");
        }

        filterChain.doFilter(request, response);
    }
}

