package com.miraclerun.petopia.controller;

import com.miraclerun.petopia.auth.JwtToken;
import com.miraclerun.petopia.auth.JwtTokenProvider;
import com.miraclerun.petopia.domain.Member;
import com.miraclerun.petopia.domain.RefreshToken;
import com.miraclerun.petopia.repository.RefreshTokenRepository;
import com.miraclerun.petopia.request.RefreshTokenRequest;
import com.miraclerun.petopia.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 토큰 재발급
     */
    @PostMapping("/refresh-tokens/members/{memberId}")
    public ResponseEntity<JwtToken> refresh(@PathVariable Long memberId, @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        RefreshToken savedToken = refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken)
                .orElseThrow(RuntimeException::new);
        Member member = savedToken.getMember();
        if (Objects.equals(member.getId(), memberId)) {
            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
                JwtToken newToken = jwtTokenProvider.generateToken(memberId, authentication);
                String accessToken = newToken.getAccessToken();
                if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                    refreshTokenService.update(savedToken, newToken.getRefreshToken());
                    return new ResponseEntity<>(newToken, HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}