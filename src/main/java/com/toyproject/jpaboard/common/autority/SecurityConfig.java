package com.toyproject.jpaboard.common.autority;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity //Spring Security를 활성화하겠다는 선언 (기본 보안 필터 체인 생성됨)
@RequiredArgsConstructor // 롬복 사용 시
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("✅ SecurityFilterChain loaded");  // 이거 찍히는지 확인

        return http
        .csrf(csrf -> csrf.disable()) // JWT 기반이므로 CSRF 비활성화
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/member/login", "/api/member/signup").permitAll() // 로그인 요청은 누구나 가능
            .anyRequest().authenticated() // 나머지는 인증 필요
        )
        .addFilterBefore((Filter) new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)// JWT 필터 등록
        .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        Spring Security 5.7 이상부터는  AuthenticationManager가 자동으로 Bean으로 등록되지 않기 때문에,
//        직접 위처럼 등록해줘야 @Autowired 또는 생성자 주입으로 사용할 수 있다.
        
        return configuration.getAuthenticationManager();
    }

}
