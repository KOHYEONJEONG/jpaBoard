package com.toyproject.jpaboard.common.service;

import com.toyproject.jpaboard.common.dto.Member;
import com.toyproject.jpaboard.common.enums.Gender;
import com.toyproject.jpaboard.common.repository.MemberRepository;
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
//    implements UserDetailsService는 Spring Security에서 인증 시 사용자 정보를 가져오는 핵심 인터페이스를 구현하겠다는 의미.
//    즉, 이걸 구현하면 **"사용자명(username)을 기반으로 사용자 정보를 불러오는 로직을 네가 직접 짜겠다"**는 뜻
//    🚩UserDetailsService는 사용자의 로그인 요청 시, DB에서 사용자 정보를 불러와야 해.

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private Map<String, String> userDb = new HashMap<>();

    @PostConstruct
    public void init() {
        // 초기 계정 등록
        userDb.put("admin", passwordEncoder.encode("admin1234"));
        //admin
        //"{bcrypt}$2a$10$Pds9l4v7gqJTOrRKmo3pn.EBkdgVXHtNE03WHIOAR7OACGfk9NS9e"
    }

    public void saveUser(String username, String password) {
        userDb.put(username, passwordEncoder.encode(password));
    }

    //JWT 또는 Spring Security에서 인증 처리를 할 때 사용자 정보를 불러오는 데 사용
    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        // 이 메서드는 Spring Security가 로그인 시 자동으로 호출해줘.
        //→ 이 안에서 DB나 메모리에서 사용자 정보를 직접 조회하고,
        //→ 그 정보를 기반으로 UserDetails 객체를 만들어 반환하는 거야.

       // 1. DB에서 사용자 조회
        //String encodedPassword = userDb.get(username);
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

//        if (encodedPassword == null) {
       if (member.getPassword() == null) {
            log.info("loadUserByUsername :: 사용자 '{}' 를 찾을 수 없습니다.", userid);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다."); //CustomAuthFailureHandler
        }

        // 2. UserDetails 객체로 생성해서 반환
        log.info("loadUserByUsername :: 사용자 '{}' 를 찾았다.", userid);
         return User //스프링 시큐리티에서 제공하는 '기본 인증 사용자 객체 클래스'
                .withUsername(userid)
                 //.password(encodedPassword)
                .password(member.getPassword())// 예 : "{bcrypt}$2a$10$Pds9l4v7gqJTOrRKmo3pn.EBkdgVXHtNE03WHIOAR7OACGfk9NS9e"
                .roles("USER")
                .build();
    }

    public Member createDefaultMember() {
        Member member = new Member(
                "admin",                  // loginId
                "{bcrypt}$2a$10$Pds9l4v7gqJTOrRKmo3pn.EBkdgVXHtNE03WHIOAR7OACGfk9NS9e"          // password (실무에서는 인코딩 필수)
                ,"admin"
                ,Gender.MAN                     // 기본 성별
        );

        return memberRepository.save(member);  // DB에 저장
    }
}