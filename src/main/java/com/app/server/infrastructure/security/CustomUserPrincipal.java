package com.app.server.infrastructure.security;

import com.app.server.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomUserPrincipal implements UserDetails,OAuth2User {

    private final User user;
    private Map<String,Object> attributes;


    public CustomUserPrincipal(User formLoginUser) {
        this.user = formLoginUser;
    }

    public CustomUserPrincipal(User oauth2LoginUser, Map<String, Object> attributes) {
        this.user = oauth2LoginUser;
        this.attributes = attributes;
    }

    // ==============================================================
    // UserDetails 구현
    // ==============================================================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_" 프리픽스는 Spring Security의 hasRole() 메소드에서 사용하기 위한 표준 접두사임. 존나 중요.
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey()));
    }

    @Override
    public String getPassword() {
        // 소셜 로그인은 비번 없으니까 null 반환. 일반 로그인은 DB에 저장된 비번 반환.
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // 로그인 ID는 이메일을 쓴다. 이게 UserDetailsService의 loadUserByUsername 파라미터로 들어감.
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부. 일단 무조건 true. 니 비즈니스 로직에 따라 바꿔라.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부. 일단 무조건 true.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격증명(비번) 만료 여부. 일단 무조건 true.
    }

    @Override
    public boolean isEnabled() {
        return !user.isDeleted(); // 탈퇴한 회원은 비활성화.
    }

    // ==============================================================
    // OAuth2User 구현
    // ==============================================================
    @Override
    public Map<String,Object> getAttributes() {
        // provider가 준 유저정보 원본. 걍 그대로 돌려주면 댐
        return this.attributes;
    }

    @Override
    public String getName() {
        // OAuth2User 에서 이 유저를 식별할 수 있는 유니크 값 전달해야댐
        // 프로바이더가 주는 providerId 쓰면 댐
        return user.getProviderId();
    }
}