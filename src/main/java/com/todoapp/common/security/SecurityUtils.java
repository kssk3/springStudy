package com.todoapp.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 현재 인증된 userId 가져오기
     * @return 현재 사용자 id
     * @throws IllegalArgumentException 인증되지 않은 경우
     */
    public static long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증된 사용자가 없습니다");
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new IllegalArgumentException("인증 정보를 찾을 수 없습니다.");
    }
}
