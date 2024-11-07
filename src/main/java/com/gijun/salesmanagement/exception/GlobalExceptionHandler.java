package com.gijun.salesmanagement.exception;

import com.gijun.salesmanagement.dto.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("Business Exception", e);
        return ResponseEntity
                .status(getStatusFromErrorCode(e.getErrorCode()))
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    // 인증 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication Exception", e);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("E401", "인증에 실패했습니다."));
    }

    // 권한 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access Denied Exception", e);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("E403", "접근 권한이 없습니다."));
    }

    // Validation 예외 처리
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class
    })
    protected ResponseEntity<ApiResponse<Void>> handleValidationException(Exception e) {
        log.error("Validation Exception", e);
        String message = "입력값이 올바르지 않습니다.";
        if (e instanceof MethodArgumentNotValidException validException) {
            message = validException.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .orElse(message);
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("E400", message));
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Internal Server Error", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("E500", "서버 에러가 발생했습니다."));
    }

    private HttpStatus getStatusFromErrorCode(String errorCode) {
        return switch (errorCode) {
            case "E400" -> HttpStatus.BAD_REQUEST;
            case "E401" -> HttpStatus.UNAUTHORIZED;
            case "E403" -> HttpStatus.FORBIDDEN;
            case "E404" -> HttpStatus.NOT_FOUND;
            case "E409" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}