package com.ufu.global.error.exception;

public class CustomJwtException extends RuntimeException {
    public CustomJwtException(String message) {
        super(message);
    }

    public static class ExpiredException extends CustomJwtException {
        public ExpiredException() {
            super("JWT 토큰이 만료되었습니다");
        }
    }

    public static class MalformedJwtException extends CustomJwtException {
        public MalformedJwtException() {
            super("JWT 구조가 적합하지 않습니다.");
        }
    }

    public static class SignatureException extends CustomJwtException {
        public SignatureException() {
            super("서명 검증에 실패하였습니다.");
        }
    }

    public static class IllegalArgumentException extends CustomJwtException {
        public IllegalArgumentException() {
            super("토큰 문자열은 null, 빈 문자열이 허용되지 않습니다.");
        }
    }
}
