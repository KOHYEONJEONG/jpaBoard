package com.toyproject.jpaboard.controller;

import com.toyproject.jpaboard.common.dto.Login;
import com.toyproject.jpaboard.common.dto.Member;
import com.toyproject.jpaboard.common.dto.RequestMemberDTO;
import com.toyproject.jpaboard.common.service.MemberService;
import com.toyproject.jpaboard.common.autority.JwtTokenProvider;
import com.toyproject.jpaboard.common.autority.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;//스프링 시큐리티 제공
import org.springframework.web.bind.annotation.*;

@Slf4j
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
        );//MemberService > loadUserByUsername 호출됨

        //토큰이 없는 상태, 로그인 시 만들어줌.
        TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);

        return ResponseEntity.ok(tokenInfo);
    }

    // 회원가입 API (토큰 발급x)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RequestMemberDTO request) {
        memberService.saveUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("회원가입 완료");
    }

    // 로그인된 사용자 정보 확인 API (토큰 확인)
    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal User user) {//org.springframework.security.core.userdetails.User
//        Spring Security가 요청을 가로채고,
//        SecurityContextHolder 에 저장된 Authentication.getPrincipal() 값을 꺼내서
//        @AuthenticationPrincipal 파라미터에 자동으로 주입해줌

        return ResponseEntity.ok("현재 사용자: " + user.getUsername());
    }

    @PostMapping("/admin-create-default")
    public ResponseEntity<String> createDefaultMember() {
        Member saved = memberService.createDefaultMember();//JPA
        return ResponseEntity.ok("초기 생성된 회원 ID: " + saved.getUserid());
    }
}