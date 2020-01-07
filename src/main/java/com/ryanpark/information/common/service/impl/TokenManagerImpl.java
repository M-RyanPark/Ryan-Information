/*
 * Copyright (c) 2020 Ryan Information Test
 */

package com.ryanpark.information.common.service.impl;

import com.ryanpark.information.common.service.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * accessToken 생성 및 갱신을 담당하는 토큰 관리자 구현체
 * rest_api grant_type 을 위하여 customize
 *
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2020-01-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenManagerImpl implements TokenManager {

	private static final String REST_API_GRANT_TYPE = "rest_api";
	private static final String OAUTH_CLIENT_ID = "loan_client";

	final UserDetailsService userDetailsService;
	final ClientDetailsService clientDetailsService;
	final TokenGranter tokenGranter;

	AuthorizationServerTokenServices tokenService;
	OAuth2RequestFactory requestFactory;

	@PostConstruct
	void postConstructor() {
		this.requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
	}

	@Autowired
	@Qualifier("defaultAuthorizationServerTokenServices")
	public void setTokenService(AuthorizationServerTokenServices tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public Optional<OAuth2AccessToken> createAccessToken(@NonNull String userId, @NonNull String password) {
		try {
			Map<String, String> oauthParam = new HashMap<>();
			oauthParam.put("client_id", OAUTH_CLIENT_ID);
			oauthParam.put("username", userId);
			oauthParam.put("password", password);

			TokenRequest tokenRequest = restApiTokenRequest(oauthParam);

			return Optional.ofNullable(tokenGranter.grant(REST_API_GRANT_TYPE, tokenRequest));
		} catch (Exception e) {
			log.error("Access Token 생성 중 에러 발생 : {}", e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Override
	public Optional<OAuth2AccessToken> refreshAccessToken(@NonNull Authentication authentication) {
		try {
			Map<String, String> oauthParam = new HashMap<>();
			oauthParam.put("client_id", OAUTH_CLIENT_ID);

			TokenRequest tokenRequest = restApiTokenRequest(oauthParam);

			ClientDetails client = clientDetailsService.loadClientByClientId(OAUTH_CLIENT_ID);
			OAuth2Request storedOAuth2Request = requestFactory.createOAuth2Request(client, tokenRequest);

			return Optional.ofNullable(tokenService.createAccessToken(new OAuth2Authentication(storedOAuth2Request, authentication)));
		} catch (Exception e) {
			log.error("Access Token Refresh 중 에러 발생 : {}", e.getMessage(), e);
			return Optional.empty();
		}
	}

	private TokenRequest restApiTokenRequest(Map<String, String> oauthParam) {
		return  new TokenRequest(
				oauthParam
				, OAUTH_CLIENT_ID,
				Arrays.asList("read", "write"), REST_API_GRANT_TYPE
		);
	}
}
