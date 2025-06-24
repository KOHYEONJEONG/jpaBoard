package com.toyproject.common.autority;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
@Component
public class JwtTokenProvider {

//    @Value("${jwt.secret}")
    private String secretKey = "DadFuFN4Oui8BfV3SCFj6R9fyJ9hD45E6AGFsXgFsRhTfYdSdS";

    private SecretKey key;

    private final long EXPIRATION_MILLISECONDS = 1000 * 60 * 60; // 예: 1시간

    // 스프링 컨테이너가 해당 빈을 생성하고 의존성 주입이 끝난 직후에 자동으로 호출되는 메서드
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
//        Base64로 인코딩된 시크릿 키 문자열을
//        byte 배열로 디코딩한 다음
//        JWT 서명을 위한 SecretKey로 변환하여 key에 저장하는 로직
//        즉, JWT 토큰을 서명하고 검증할 때 사용할 실제 키 객체를 만드는 초기화 코드
    }

    // 토큰 생성
    public TokenInfo createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); //, 콤마를 기준으로 String 추출

        //만료시간
        Date now = new Date();
        Date accessExpiration = new Date(now.getTime() + EXPIRATION_MILLISECONDS);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities) //클레임에 auth 키값 넣기(클레임에 권한 남기)
                .setIssuedAt(now)
                .setExpiration(accessExpiration)
                .signWith(key, SignatureAlgorithm.HS256) //알고리즘
                .compact();

        return new TokenInfo("Bearer", accessToken);

    }

    // 토큰 정보 추출
    public Authentication getAuthentication(String token) {//token은 access token이다.
        Claims claims = getClaims(token);

        Object authObj = claims.get("auth");
        if (authObj == null) {
            throw new RuntimeException("잘못된 토큰입니다.");
        }

        String auth = authObj.toString();

        // 권한 정보 추출(클레임에서 권한 문자열을 추출하고, 쉼표(,)로 분리한 후 SimpleGrantedAuthority 반환)
        Collection<SimpleGrantedAuthority> authorities =
                Arrays.stream(auth.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        //UsernamePasswordAuthenticationToken은 스프링 시큐리티의 Authentication 구현체로, 인증 객체를 만들어 반환
        //ID/PW 기반 인증 정보를 담기 위해 사용돼.
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    //토큰 안에 클레임 꺼내오기.(클레임 꺼내오면 이 안에 auth 를 추출할 수 있다.)
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // key는 SecretKey 타입의 필드라고 가정
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);  // 유효성 검사 목적으로 Claims만 파싱
            return true;
            
//            작업중 false 시  -> 작업 별로 Exception이 떨어지면 아래 catch  실행됨
        } catch (SecurityException e) {
            // Invalid JWT Signature
            throw new RuntimeException("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            // Invalid JWT token
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            // Expired JWT token
            throw new RuntimeException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            // Unsupported JWT token
            throw new RuntimeException("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            // JWT claims string is empty
            throw new RuntimeException("JWT 클레임이 비어있습니다.");
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("JWT 처리 중 알 수 없는 오류가 발생했습니다.");
        }

//        return false;
    }

}