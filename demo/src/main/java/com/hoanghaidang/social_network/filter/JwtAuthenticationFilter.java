package com.hoanghaidang.social_network.filter;


import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.IUserSecurityService;
import com.hoanghaidang.social_network.service.impl.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserSecurityService iUserSecurityService;

    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                authHeader = request.getHeader("refreshToken");
            }
            String token = null;
            String email = null;
            String tokenType = null;
            if (authHeader != null && (authHeader.startsWith("Bearer ") || authHeader.startsWith("Refresh-Token"))) {
                token = authHeader.substring(authHeader.startsWith("Bearer ") ? 7 : 14);
                tokenType = authHeader.startsWith("Bearer ") ? JwtService.SECRET_ACCESS_TOKEN : JwtService.SECRET_REFRESH_TOKEN;

                email = jwtService.extractEmail(token, tokenType);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = iUserSecurityService.loadUserByUsername(email);

                boolean isTokenValid = jwtService.validateToken(token, userDetails, tokenType);
                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (CustomException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            resolver.resolveException(request, response, null, e);
        }
    }

}
