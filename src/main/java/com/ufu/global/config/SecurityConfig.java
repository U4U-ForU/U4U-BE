package com.ufu.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufu.global.error.ErrorResponse;
import com.ufu.global.security.jwt.JwtAuthenticationFilter;
import com.ufu.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf ->
                        csrf.disable())
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource()))
                .formLogin(form ->
                        form.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint((request, response, authException) ->
                                        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "인증이 필요합니다"))
                                .accessDeniedHandler((request, response, accessDeniedException) ->
                                        sendErrorResponse(response, HttpStatus.FORBIDDEN, "접근 권한이 없습니다"))
                )

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**"
                                ).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/items/submissions").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/submissions/me").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/submissions/{submissionId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/items/submissions/{submissionId}/cancel").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/me").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/me/{itemId}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/me/trading").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/admin/items").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/admin/items/submissions/{submissionId}/combine").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/admin/cauldron/recipes").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/admin/cauldron/recipes").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/admin/cauldron/recipes/{recipeId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/admin/cauldron/recipes/{recipeId}").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/admin/cauldron/recipes/{recipeId}").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/trades").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/trades").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/trades/{tradeId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/trades/{tradeId}/title").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/trades/{tradeId}").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/trades/{tradeId}/comments").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/trades/{tradeId}/comments/{commentId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/trades/{tradeId}/comments/{commentId}/accept").authenticated()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, objectMapper),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message)
            throws java.io.IOException {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                ErrorResponse.of(httpStatus.value(), message)
        ));
    }
}
