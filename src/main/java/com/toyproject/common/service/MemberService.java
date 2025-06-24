package com.toyproject.common.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private Map<String, String> userDb = new HashMap<>();

    @PostConstruct
    public void init() {
        // 초기 계정 등록
        userDb.put("admin", passwordEncoder.encode("admin1234"));
    }

    public void saveUser(String username, String password) {
        userDb.put(username, passwordEncoder.encode(password));
    }

    //JWT 또는 Spring Security에서 인증 처리를 할 때 사용자 정보를 불러오는 데 사용
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String encodedPassword = userDb.get(username);
        if (encodedPassword == null) {
            log.debug("loadUserByUsername :: 사용자를 찾을 수 없습니다.");
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다."); //CustomAuthFailureHandler
        }
        return User.withUsername(username)
                .password(encodedPassword)
                .roles("USER")
                .build();
    }
}