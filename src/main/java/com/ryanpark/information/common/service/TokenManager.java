/*
 * Copyright (c) 2020 Ryan Information Test
 */

package com.ryanpark.information.common.service;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Optional;

/**
 * accessToken 생성 및 갱신을 담당하는 토큰 관리자
 *
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2020-01-07
 */
public interface TokenManager {
	Optional<OAuth2AccessToken> createAccessToken(@NonNull String userId, @NonNull String password);
	Optional<OAuth2AccessToken> refreshAccessToken(@NonNull Authentication authentication);
}
