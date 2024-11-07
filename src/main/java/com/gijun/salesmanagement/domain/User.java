package com.gijun.salesmanagement.domain;

import com.gijun.salesmanagement.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }

    @Builder(toBuilder = true)
    public User(String email, String password, String name, String phone, Role role) {
        this.email = Objects.requireNonNull(email, "이메일은 필수입니다.");
        this.password = Objects.requireNonNull(password, "비밀번호는 필수입니다.");
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.phone = Objects.requireNonNull(phone, "전화번호는 필수입니다.");
        this.role = Objects.requireNonNullElse(role, Role.ROLE_USER);
    }

    public void updateProfile(String name, String phone) {
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.phone = Objects.requireNonNull(phone, "전화번호는 필수입니다.");
    }

    public void updatePassword(String newPassword) {
        this.password = Objects.requireNonNull(newPassword, "비밀번호는 필수입니다.");
    }

    public void updateRole(Role role) {
        this.role = Objects.requireNonNull(role, "권한은 필수입니다.");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}