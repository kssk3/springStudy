package com.todoapp.common.security;

import org.springframework.stereotype.Component;

@Component
public class SpringSecurityCurrentUserIdProvider implements CurrentUserIdProvider {
    @Override
    public long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
}
