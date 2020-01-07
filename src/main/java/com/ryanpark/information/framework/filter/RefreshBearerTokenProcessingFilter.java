/*
 * Copyright (c) 2020 Ryan Information Test
 */

package com.ryanpark.information.framework.filter;

import com.ryanpark.information.common.service.TokenManager;
import com.ryanpark.information.framework.oauth.RefreshBearerTokenExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * {@link org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter}
 * 다음 순서에 위치하며 "Bearer Token" Authorization Header에서 정상 처리된 인증 건에 대해서만
 * Response Header 에 새로운 accessToken을 발급하여 전달 한다
 *
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2020-01-05
 */
@Slf4j
@RequiredArgsConstructor
public class RefreshBearerTokenProcessingFilter extends GenericFilterBean {

	public static final String HEADER_NAME = "X_REFRESHED_ACCESS_TOKEN";
	final TokenManager tokenManager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;

		if (shouldAddRefreshedAccessTokenHeader(httpServletRequest)) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			log.info("authentication : {}", authentication);

			tokenManager.refreshAccessToken(authentication)
					.ifPresent(
							x -> httpServletResponse.addHeader(HEADER_NAME, x.getValue())
					);
		}

		chain.doFilter(request, response);
	}

	private boolean shouldAddRefreshedAccessTokenHeader(HttpServletRequest httpServletRequest) {
		return Optional.ofNullable(httpServletRequest.getAttribute(RefreshBearerTokenExtractor.BEARER_REFRESH_REQUIRED))
				.map(Boolean.class::cast)
				.orElse(false)
				;
	}
}
