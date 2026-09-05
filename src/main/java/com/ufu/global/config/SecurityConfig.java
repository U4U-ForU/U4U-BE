package com.ufu.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufu.global.error.ErrorResponse;
import com.ufu.global.security.jwt.JwtAuthenticationFilter;
import com.ufu.domain.user.domain.Role;
import com.ufu.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    // 콤마로 구분된 환경변수 값을 Spring이 List로 변환해 주입한다.
    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
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
                                // 인증
                                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()

                                // API 문서
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**"
                                ).permitAll()

                                // 아이템 제출
                                .requestMatchers(HttpMethod.POST, "/api/items/submissions").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/submissions/me").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/submissions/{submissionId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/items/submissions/{submissionId}/cancel").authenticated()

                                // 보유 아이템
                                .requestMatchers(HttpMethod.GET, "/api/items/me").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/me/{itemId}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/items/me/trading").authenticated()

                                // 관리자 - 아이템 조회
                                .requestMatchers(HttpMethod.GET, "/api/admin/items").hasAuthority(Role.ADMIN.name())

                                // 관리자 - 아이템 제출 심사
                                .requestMatchers(HttpMethod.GET, "/api/admin/items/submissions").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/api/admin/items/submissions/{submissionId}").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/api/admin/items/submissions/{submissionId}/approve").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/api/admin/items/submissions/{submissionId}/combine").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/api/admin/items/submissions/{submissionId}/reject").hasAuthority(Role.ADMIN.name())

                                // 관리자 - 조합법 관리
                                .requestMatchers(HttpMethod.POST, "/api/admin/cauldron/recipes").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/api/admin/cauldron/recipes").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/api/admin/cauldron/recipes/{recipeId}").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/api/admin/cauldron/recipes/{recipeId}").hasAuthority(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/api/admin/cauldron/recipes/{recipeId}").hasAuthority(Role.ADMIN.name())

                                // 위에 명시하지 않은 관리자 경로가 로그인 사용자에게 열리지 않도록 막는다
                                .requestMatchers("/api/admin/**").hasAuthority(Role.ADMIN.name())

                                // 가마솥
                                .requestMatchers(HttpMethod.GET, "/api/cauldron/recipes").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/cauldron/recipes/{recipeId}").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/cauldron/recombine").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/cauldron/mix").authenticated()

                                // 거래
                                .requestMatchers(HttpMethod.POST, "/api/trades").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/trades").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/trades/{tradeId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/trades/{tradeId}/title").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/trades/{tradeId}").authenticated()

                                // 거래 제안
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
