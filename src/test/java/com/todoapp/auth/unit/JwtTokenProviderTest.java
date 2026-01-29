package com.todoapp.auth.unit;

import static org.assertj.core.api.Assertions.*;

import com.todoapp.common.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "test-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm";
        long accessTokenExpiration = 3600000L;  // 1시간
        long refreshTokenExpiration = 604800000L;  // 7일

        jwtTokenProvider = new JwtTokenProvider(
                secret,
                accessTokenExpiration,
                refreshTokenExpiration
        );
    }

    @Test
    @DisplayName("Access Token 생성 성공")
    void generateAccessToken_Success() {
        // given
        String email = "test123@gmail.net";

        // when
        String token = jwtTokenProvider.generateAccessToken(email);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Refresh Token 생성 성공")
    void generateRefreshToken_Success() {
        // given
        String email = "test123@gmail.net";

        // when
        String token = jwtTokenProvider.generateRefreshToken(email);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("유효한 토큰에서 이메일 추출")
    void getEmailFromToken_Success() {
        // given
        String email = "test123@gmail.net";
        String token = jwtTokenProvider.generateAccessToken(email);

        // when
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
        // then
        assertThat(email).isEqualTo(extractedEmail);
    }

    @Test
    @DisplayName("발급된 토큰이 유효하면 True 반환한다")
    void validateToken_returnsTrue_whenTokenWasGeneratedByProvider() {
        // given
        String email = "test123@gmail.net";

        // when
        String token = jwtTokenProvider.generateAccessToken(email);

        // then
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증 시 False 반환")
    void validateToken_InvalidToken_ReturnsFalse_whenTokenWasGeneratedByProvider() {
        // given
        String notGeneratedToken = "test123";

        // when & then
        assertThat(jwtTokenProvider.validateToken(notGeneratedToken)).isFalse();
    }

    @Test
    @DisplayName("빈 토큰 검증 실패")
    void validateToken_EmptyToken_ReturnsFalse() {
        // given
        String token = "";

        assertThat(jwtTokenProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("Access Token 과 Refresh Token은 서로 다름")
    void tokens_AreDifferent() {
        // given
        String email = "test123@gmail.net";

        // when
        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        // then
        assertThat(accessToken).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("같은 이메일로 토큰을 여러 번 생성하면 매번 다른 토큰 ")
    void generateToken_MultipleTimes_ReturnsDifferentTokens() throws InterruptedException {
        // given
        String email = "test123@gmail.net";

        String accessTokenA = jwtTokenProvider.generateAccessToken(email);
        Thread.sleep(1000);
        String accessTokenB = jwtTokenProvider.generateAccessToken(email);

        assertThat(accessTokenA).isNotEqualTo(accessTokenB);
    }
}