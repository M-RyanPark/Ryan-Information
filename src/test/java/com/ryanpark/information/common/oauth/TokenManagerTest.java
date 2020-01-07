/*
 * Copyright (c) 2020 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import com.ryanpark.information.common.service.TokenManager;
import com.ryanpark.information.common.service.impl.TokenManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

/**
 * TokenManger Test
 *
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2020-01-07
 */
@SpringBootTest(classes = TokenManagerImpl.class)
public class TokenManagerTest {
	@MockBean UserDetailsService userDetailsService;
	@MockBean ClientDetailsService clientDetailsService;
	@MockBean AuthenticationManager authenticationManager;
	@MockBean @Qualifier("defaultAuthorizationServerTokenServices")
	AuthorizationServerTokenServices tokenService;
	@MockBean TokenGranter tokenGranter;

	@Autowired TokenManager tokenManager;

	@BeforeEach
	public void setUp() {

	}

	@Test
	public void success_create_access_token() {
		OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
		given(tokenGranter.grant(anyString(), any(TokenRequest.class)))
				.willReturn(oAuth2AccessToken);

		Optional<OAuth2AccessToken> actual = tokenManager.createAccessToken("testId", "testPwd");

		assertTrue(actual.isPresent());
	}

	@Test
	public void fail_create_access_token() {
		OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
		given(tokenGranter.grant(anyString(), any(TokenRequest.class)))
				.willReturn(null);

		Optional<OAuth2AccessToken> actual = tokenManager.createAccessToken("testId", "testPwd");

		assertFalse(actual.isPresent());
	}

	@Test
	public void success_refresh_access_token() {
		ClientDetails clientDetails = mock(ClientDetails.class);

		given(clientDetails.getClientId())
				.willReturn("loan_client");
		given(clientDetails.getAuthorities())
				.willReturn(new ArrayList<>());
		given(clientDetails.getResourceIds())
				.willReturn(new HashSet<>());

		given(clientDetailsService.loadClientByClientId(anyString()))
				.willReturn(clientDetails);
		given(tokenService.createAccessToken(any()))
				.willReturn(mock(OAuth2AccessToken.class));

		Optional<OAuth2AccessToken> actual = tokenManager.refreshAccessToken(mock(Authentication.class));

		assertTrue(actual.isPresent());
	}

	@Test
	public void fail_refresh_access_token() {
		ClientDetails clientDetails = mock(ClientDetails.class);

		given(clientDetails.getClientId())
				.willReturn("loan_client");
		given(clientDetails.getAuthorities())
				.willReturn(new ArrayList<>());
		given(clientDetails.getResourceIds())
				.willReturn(new HashSet<>());

		given(clientDetailsService.loadClientByClientId(anyString()))
				.willReturn(clientDetails);
		given(tokenService.createAccessToken(any()))
				.willReturn(null);

		Optional<OAuth2AccessToken> actual = tokenManager.refreshAccessToken(mock(Authentication.class));

		assertFalse(actual.isPresent());
	}
}
