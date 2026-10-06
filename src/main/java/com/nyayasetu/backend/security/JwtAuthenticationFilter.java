package com.nyayasetu.backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // TEST: this should ALWAYS appear in Eclipse console
        System.out.println(
                "========== JWT FILTER CALLED =========="
        );

        System.out.println(
                "Request: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );

        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "Authorization Header: " + authHeader
        );

        String token = null;
        String email = null;

        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            System.out.println("Bearer token found");

            try {

                email = jwtService.extractEmail(token);

                System.out.println(
                        "JWT Email: " + email
                );

            } catch (Exception e) {

                System.out.println(
                        "========== JWT ERROR =========="
                );

                e.printStackTrace();
            }
        } else {

            System.out.println(
                    "No Bearer token found"
            );
        }

        if (email != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            try {

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(email);

                System.out.println(
                        "User found: "
                                + userDetails.getUsername()
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println(
                        "========== JWT AUTHENTICATION SUCCESS =========="
                );

            } catch (Exception e) {

                System.out.println(
                        "========== USER AUTHENTICATION ERROR =========="
                );

                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}