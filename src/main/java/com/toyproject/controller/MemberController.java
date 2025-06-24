package com.toyproject.controller;

import com.toyproject.common.dto.Login;
import com.toyproject.common.dto.User;
import com.toyproject.common.service.MemberService;
import com.toyproject.common.autority.JwtTokenProvider;
import com.toyproject.common.autority.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member/")
public class MemberController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    // 로그인 API (토큰 발급o)
    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@RequestBody Login request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        //토큰이 없는 상태, 로그인 시 만들어줌.
        TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);
        return ResponseEntity.ok(tokenInfo);
    }

    // 회원가입 API (토큰 발급x)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User request) {
        memberService.saveUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("회원가입 완료");
    }

    // 로그인된 사용자 정보 확인 API
    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        return ResponseEntity.ok("현재 사용자: " + user.getUsername());
    }
}