package com.ufu.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufu.global.error.ErrorResponse;
import com.ufu.global.error.exception.CustomJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/auth/login")
                || path.equals("/api/auth/signup")
                || path.equals("/api/auth/refresh");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        try {
            String parseToken = jwtTokenProvider.resolveToken(request);
            if (parseToken != null && jwtTokenProvider.validateToken(parseToken, JwtTokenProvider.ACCESS_TYPE)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(parseToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (CustomJwtException e) {
            log.warn("JWT Exception : {}", e.getMessage());
            sendErrorResponse(response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = objectMapper.writeValueAsString(
                ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), message)
        );
        response.getWriter().write(json);
    }
}
