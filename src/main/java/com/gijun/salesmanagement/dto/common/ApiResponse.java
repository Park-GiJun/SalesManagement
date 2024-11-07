package com.gijun.salesmanagement.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        Error error,
        LocalDateTime timestamp
) {
    @Builder
    public record Error(
            String code,
            String message,
            String detail
    ) {}

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, LocalDateTime.now());
    }

    public static ApiResponse<Void> successResponse() {
        return new ApiResponse<>(true, null, null, LocalDateTime.now());
    }

    public static ApiResponse<Void> error(String code, String message, String detail) {
        return new ApiResponse<>(false, null,
                new Error(code, message, detail), LocalDateTime.now());
    }

    public static ApiResponse<Void> error(String code, String message) {
        return error(code, message, null);
    }
}