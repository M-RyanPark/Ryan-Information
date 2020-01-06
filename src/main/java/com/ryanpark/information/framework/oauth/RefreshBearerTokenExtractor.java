/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-28
 * description : Authorization Header에 Bearer Token 으로 요청이 올 경우 access Token Refresh를 처리하기 위해
 * HttpServletRequest 에 Bearer Refresh라 Attribute를 설정해서 넘겨준다.
 * OAuth2AuthenticationProcessingFilter 에서 token 인증을 처리 후 Post Filter에서 새 토큰을 발급하여 처리 한다
 */
public class RefreshBearerTokenExtractor extends BearerTokenExtractor {

	public static final String BEARER_REFRESH_TYPE = "Bearer Token";
	public static final String BEARER_REFRESH_REQUIRED = "Bearer Refresh";

	@Override
	protected String extractHeaderToken(HttpServletRequest request) {

		Optional<AccessTokenHeader> optionalAccessTokenHeader = extractOauthAuthorizationHeader(request);

		optionalAccessTokenHeader.ifPresent(x -> {
			request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, x.getAccessToken());
			if (x.needRefresh) {
				request.setAttribute(BEARER_REFRESH_REQUIRED, true);
			}
		});

		return optionalAccessTokenHeader
				.map(x -> removePostCommaValue(x.getAccessToken()))
				.orElse(null)
				;
	}

	protected Optional<AccessTokenHeader> extractOauthAuthorizationHeader(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders("Authorization");

		// todo Authorization Token 헤더가 2개 이상일땐 어떻게 해야 할까 ?
		return Collections.list(headers).stream()
				.filter(header -> isBearerTokenHeader(header) || isBearerRefreshTokenHeader(header))
				.min((s1, s2) ->
						isBearerRefreshTokenHeader(s1) ^ isBearerRefreshTokenHeader(s2)
								? (isBearerTokenHeader(s1) && isBearerRefreshTokenHeader(s2)  ? 1 : -1)
								: 0
				)
				.map(header -> new AccessTokenHeader(extractTokenValue(header), isBearerRefreshTokenHeader(header)))
				;
	}

	private boolean isBearerTokenHeader(String headerValue) {
		return headerValue.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase());
	}

	private boolean isBearerRefreshTokenHeader(String headerValue) {
		return headerValue.toLowerCase().startsWith(BEARER_REFRESH_TYPE.toLowerCase());
	}

	private String extractTokenValue(@NonNull String headerValue) {
		int tokenValueIndex = isBearerRefreshTokenHeader(headerValue) ?
				BEARER_REFRESH_TYPE.length() : OAuth2AccessToken.BEARER_TYPE.length();

		return headerValue.substring(tokenValueIndex).trim();
	}

	private String removePostCommaValue(@NonNull String tokenValue) {
		int commaIndex = tokenValue.indexOf(',');

		return commaIndex > 0 ? tokenValue.substring(0, commaIndex).trim() : tokenValue;
	}

	@Setter
	@Getter
	@RequiredArgsConstructor
	static class AccessTokenHeader {
		final String accessToken;
		final boolean needRefresh;
	}
}
