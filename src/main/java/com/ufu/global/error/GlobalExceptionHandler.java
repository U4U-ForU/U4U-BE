package com.ufu.global.error;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.CustomJwtException;
import com.ufu.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 4xx는 요청 쪽 문제라 warn, 5xx(예: FILE_UPLOAD_FAILED)는 서버 장애라 error로 남긴다.
    // 싱글턴 예외의 스택은 실제 발생 위치를 가리키지 않으므로 남기지 않는다.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        if (errorCode.getStatus() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.error("handleBusinessException: {}", exception.getMessage());
        } else {
            log.warn("handleBusinessException: {}", exception.getMessage());
        }

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    // 필터를 거치지 않는 /api/auth/refresh 에서도 토큰 예외가 401로 나가도록 처리한다.
    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ErrorResponse> handleCustomJwtException(CustomJwtException exception) {
        log.warn("handleCustomJwtException: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        log.warn("handleMethodArgumentNotValidException: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), getErrorMessage(exception)));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException exception) {
        log.warn("handleBindException: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), getErrorMessage(exception)));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException exception) {
        log.warn("handleMaxUploadSizeExceededException: {}", exception.getMessage());
        ErrorCode errorCode = ErrorCode.FILE_SIZE_EXCEEDED;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    // sort, status 같은 enum 쿼리 파라미터에 정의되지 않은 값이 들어오면 발생한다.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {
        log.warn("handleMethodArgumentTypeMismatchException: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getName() + " 값이 올바르지 않습니다"
                ));
    }

    private String getErrorMessage(BindException exception) {
        return exception.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse("요청 값이 올바르지 않습니다");
    }
}
