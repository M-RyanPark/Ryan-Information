/*
 * Copyright (c) 2020 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import com.ryanpark.information.common.service.TokenManager;
import com.ryanpark.information.framework.filter.RefreshBearerTokenProcessingFilter;
import com.ryanpark.information.framework.oauth.RefreshBearerTokenExtractor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.spy;

/**
 * {@link RefreshBearerTokenProcessingFilter} 테스트
 *
 * {@link org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter}
 * 에서 넘겨준 attribute 값이 존 재할 경우 정상적으로 token을 refresh 하고 response header에 전달 하는지 검증
 *
 * attribute 값이 없을 경우 {@see TokenManager#refreshAccessToken} 호출 하지 않는지 검증
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2020-01-07
 */
@Slf4j
public class RefreshBearerTokenProcessingFilterTest {

	TokenManager tokenManager = mock(TokenManager.class);
	RefreshBearerTokenProcessingFilter filter = new RefreshBearerTokenProcessingFilter(tokenManager);

	@BeforeEach
	void setUp() {
		SecurityContext securityContext = mock(SecurityContext.class);
		given(securityContext.getAuthentication())
				.willReturn(mock(Authentication.class));
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	public void success_add_refreshed_token_header_in_response() throws IOException, ServletException {
		MockHttpServletRequest spyRequest = spy(new MockHttpServletRequest());
		MockHttpServletResponse spyResponse = spy(new MockHttpServletResponse());
		OAuth2AccessToken mockToken = mock(OAuth2AccessToken.class);
		String tokenValue= "XXXXX";
		given(spyRequest.getAttribute(RefreshBearerTokenExtractor.BEARER_REFRESH_REQUIRED))
				.willReturn(true);
		given(mockToken.getValue())
				.willReturn(tokenValue);
		given(tokenManager.refreshAccessToken(any()))
				.willReturn(Optional.of(mockToken));

		filter.doFilter(spyRequest, spyResponse, mock(FilterChain.class));

		assertEquals(tokenValue, spyResponse.getHeader(RefreshBearerTokenProcessingFilter.HEADER_NAME));
		then(spyResponse).should()
				.addHeader(anyString(), anyString());
	}

	@Test
	public void expect_no_action_if_attributes_is_not_exist() throws IOException, ServletException {
		MockHttpServletRequest spyRequest = spy(new MockHttpServletRequest());
		MockHttpServletResponse spyResponse = spy(new MockHttpServletResponse());
		OAuth2AccessToken mockToken = mock(OAuth2AccessToken.class);

		given(spyRequest.getAttribute(RefreshBearerTokenExtractor.BEARER_REFRESH_REQUIRED))
				.willReturn(false);

		filter.doFilter(spyRequest, spyResponse, mock(FilterChain.class));

		then(tokenManager).should(never())
				.refreshAccessToken(any());
		then(spyResponse).should(never())
				.addHeader(anyString(), anyString());

	}
}
