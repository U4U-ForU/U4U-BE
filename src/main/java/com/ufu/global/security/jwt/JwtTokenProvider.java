package com.ufu.global.security.jwt;

import com.ufu.domain.auth.presentation.dto.response.TokenResponse;
import com.ufu.domain.user.domain.RefreshToken;
import com.ufu.domain.user.domain.Role;
import com.ufu.domain.user.repository.RefreshTokenRepository;
import com.ufu.global.error.exception.CustomJwtException;
import com.ufu.global.security.auth.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    public static final String ACCESS_TYPE = "access";
    public static final String REFRESH_TYPE = "refresh";

    private static final String TYPE_HEADER = "type";

    private final JwtProperty jwtProperty;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponse generateBothToken(String loginId, Role role) {
        String accessToken = generateAccessToken(loginId, role);
        String refreshToken = generateRefreshToken(loginId, role);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(role)
                .build();
    }

    public String generateAccessToken(String loginId, Role role) {
        return generateToken(loginId, role, ACCESS_TYPE, jwtProperty.getAccessExp());
    }

    public String generateRefreshToken(String loginId, Role role) {
        String refreshToken = generateToken(loginId, role, REFRESH_TYPE, jwtProperty.getRefreshExp());
        refreshTokenRepository.save(RefreshToken.builder()
                .loginId(loginId)
                .token(refreshToken)
                .ttl(jwtProperty.getRefreshExp())
                .build());

        return refreshToken;
    }

    private String generateToken(String loginId, Role role, String type, Long exp) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, jwtProperty.getSecretKey())
                .setSubject(loginId)
                .setHeaderParam(TYPE_HEADER, type)
                .claim("authority", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + exp * 1000))
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerWithValue = request.getHeader(jwtProperty.getHeader());
        return parseToken(bearerWithValue);
    }

    private String parseToken(String bearerWithValue) {
        if (bearerWithValue != null && bearerWithValue.startsWith(jwtProperty.getPrefix())) {
            return bearerWithValue.replace(jwtProperty.getPrefix(), "");
        }
        return null;
    }

    public boolean validateToken(String token, String expectedType) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(jwtProperty.getSecretKey())
                    .build()
                    .parseClaimsJws(token);

            if (!expectedType.equals(jws.getHeader().get(TYPE_HEADER))) {
                throw new CustomJwtException.InvalidTypeException();
            }

            return true;
        } catch (SignatureException e) {
            throw new CustomJwtException.SignatureException();
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException.ExpiredException();
        } catch (MalformedJwtException e) {
            throw new CustomJwtException.MalformedJwtException();
        } catch (IllegalArgumentException e) {
            throw new CustomJwtException.IllegalArgumentException();
        }
    }

    public String getSubject(String token) {
        return getTokenBody(token).getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getTokenBody(token).getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private Claims getTokenBody(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperty.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
