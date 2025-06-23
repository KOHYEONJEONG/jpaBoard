package common.autority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * filter로 생서한 이유
 * - Spring Security는 내부적으로 다음과 같은 필터 체인(Filter Chain) 구조로 작동
 * - 컨트롤러 도달 전에 요청을 가로채서 JWT 유효성 검증을 할 수 있다.
 *
 * JWT는 Stateless(상태 없음) 인증 방식이야. 그래서:
 * 세션/쿠키를 사용하지 않고
 * 요청마다 JWT 토큰을 Authorization 헤더에 실어서 보냄
 * 서버는 필터에서 토큰을 파싱하고 사용자 인증 처리(스프링 시큐리티는 필터에서 검증함)
 * -> 이걸 Interceptor나 Controller에서 하면 이미 늦음.
 *
 * [세션]
 * 1. 로그인 성공 → 서버가 세션 생성 → 세션ID를 브라우저에 저장 (쿠키)
 * 2. 다음 요청 → 서버가 세션ID로 사용자 정보를 기억해서 인증
 *
 * [JWT]
 * 1. 로그인 성공 → 서버가 JWT(사용자 정보 + 서명) 발급
 * 2. 클라이언트는 JWT를 가지고 있음 (쿠키 아님, 로컬 저장소나 메모리에 저장됨)
 * 3. 매 요청 시 → JWT를 Authorization 헤더에 실어서 보냄
 * 4. 서버는 → JWT를 "파싱하고 서명 검증"만 하고, 상태를 따로 저장하지 않음
 *
 * 필터는 회사의 정문 경비 아저씨이다.
 * 인터셉터는 층마다 있는 비서, 권한 확인 및 인가 보조(신분증을 여기서 확인하면 늦음, 필터(로비)에서 확인하는게 맞지?).
 * 컨트롤러는 부장님(최종 담당자).
 * */

public class JwtAuthenticationFilter {
    //토큰 검사 필터
    
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        //필터 로직, s
        String token = resolveToken((HttpServletRequest) request);//access token

        //토큰이 정상적인 토큰이면 정보를 뽑아옴
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);//⭐핵심 : 스프링 시큐리티가 인증된 사용자로 인식함.
        }
        //필터 로직, e

        // 인증 정보 설정을 마쳤으면 다음 필터 or Controller로 넘긴다
        chain.doFilter(request, response); // 여기서 DispatcherServlet에게 넘김, Spring MVC로 흐름 넘기기.

        /*
        * 클라이언트 요청
              ↓
        🧱 Filter (ex. JwtAuthenticationFilter)
              ↓  ← 여기서 chain.doFilter(request, response)
        🧱 DispatcherServlet
              ↓
        🧱 HandlerInterceptor (preHandle)
              ↓
        🎯 Controller (@RestController 등)
        * */
    }

    //request 로부터 header 에 Authorization으로 가지고 있는 문자를 가지고 와서 맞다면 뒤에 이쓴ㄴ key값만 뽑아오는 역할
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        //Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...

        //"Bearer "라는 접두사가 있어야 유효한 토큰임(다른 값이면 공격 가능성도 있음 → 거부해야 함)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // (hasText) : 요청 헤더가 아예 없거나 비어 있을 수도 있음
            return bearerToken.substring(7); // "Bearer "(공백까지 7글자) 이후의 토큰 추출
        }

        return null;
    }
}
