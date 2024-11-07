package com.gijun.salesmanagement.service;

import com.gijun.salesmanagement.domain.User;
import com.gijun.salesmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자의 정보를 가져옵니다.
     * @return User 현재 인증된 사용자
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     * @throws IllegalStateException 인증 정보가 없는 경우
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + authentication.getName()));
    }

    /**
     * 이메일로 사용자를 조회합니다.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

    /**
     * 사용자 ID로 사용자를 조회합니다.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 사용자가 관리자 권한을 가지고 있는지 확인합니다.
     */
    public boolean isAdmin(User user) {
        return user.getRole() == User.Role.ROLE_ADMIN;
    }

    /**
     * 현재 사용자가 관리자 권한을 가지고 있는지 확인합니다.
     */
    public boolean currentUserIsAdmin() {
        return isAdmin(getCurrentUser());
    }
}