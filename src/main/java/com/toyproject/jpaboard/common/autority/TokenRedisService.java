package com.toyproject.jpaboard.common.autority;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "RT:"; // Redis Key 접두사

    /**
     * Refresh Token 저장
     */
    public void saveRefreshToken(String username, String refreshToken, long duration, TimeUnit unit) {
        String key = PREFIX + username;
        redisTemplate.opsForValue().set(key, refreshToken, duration, unit);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String username) {
        String key = PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Refresh Token 삭제 (로그아웃 시 등)
     */
    public void deleteRefreshToken(String username) {
        String key = PREFIX + username;
        redisTemplate.delete(key);
    }

    /**
     * Refresh Token 존재 여부
     */
    public boolean hasRefreshToken(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + username));
    }
}
