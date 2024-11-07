package com.gijun.salesmanagement.service;

import com.gijun.salesmanagement.domain.User;
import com.gijun.salesmanagement.dto.AuthDto;
import com.gijun.salesmanagement.exception.EntityNotFoundException;
import com.gijun.salesmanagement.repository.UserRepository;
import com.gijun.salesmanagement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(AuthDto.SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .phone(request.phone())
                .role(User.Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        // 1. Login Email/PW 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // 2. 실제 검증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 4. 사용자 정보 가져오기
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return new AuthDto.LoginResponse(
                accessToken,
                refreshToken,
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}