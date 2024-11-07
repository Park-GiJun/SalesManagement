package com.gijun.salesmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthDto() {

    public record SignUpRequest(
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "유효한 이메일 주소를 입력해주세요.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                    message = "비밀번호는 8자 이상의 영문자, 숫자, 특수문자를 포함해야 합니다.")
            String password,

            @NotBlank(message = "이름은 필수입니다.")
            String name,

            @NotBlank(message = "전화번호는 필수입니다.")
            @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다.")
            String phone
    ) {}

    public record LoginRequest(
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "유효한 이메일 주소를 입력해주세요.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            String password
    ) {}

    public record LoginResponse(
            String accessToken,
            String refreshToken,
            String email,
            String name,
            String role
    ) {}

    public record TokenResponse(
            String accessToken,
            String refreshToken
    ) {}
}