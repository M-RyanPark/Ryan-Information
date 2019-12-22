/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import com.ryanpark.information.common.domain.api.SignInRequest;
import com.ryanpark.information.common.domain.api.SignInResponse;
import com.ryanpark.information.common.domain.api.SignUpRequest;
import com.ryanpark.information.common.domain.api.SignUpResponse;
import com.ryanpark.information.common.repository.entity.Account;
import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.common.service.SignService;
import com.ryanpark.information.common.service.impl.SignServiceImpl;
import com.ryanpark.information.framework.exception.BadRequestException;
import com.ryanpark.information.framework.exception.BusinessValidationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : SignService Test
 */
@SpringBootTest(classes = SignServiceImpl.class)
@Slf4j
public class SignServiceTest {

	@Autowired SignService signService;

	@MockBean AccountService accountService;
	@MockBean TokenGranter tokenGranter;

	OAuth2AccessToken oAuth2AccessToken;

	@BeforeEach
	public void setup() {
		oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);

		given(tokenGranter.grant(anyString(), any(TokenRequest.class))).willReturn(oAuth2AccessToken);
	}

	@Test
	public void sign_up_success() {
		SignUpRequest signUpRequest = mockSignUpRequest("test1");

		given(accountService.createAccount(anyString(), anyString()))
				.willReturn(Account.of(signUpRequest.getUserId(), signUpRequest.getPassword()));

		SignUpResponse response = signService.signUp(signUpRequest);

		assertNotNull(response);

		verify(accountService).createAccount(anyString(), anyString());
		verify(tokenGranter).grant(anyString(), any(TokenRequest.class));
	}

	@Test
	public void sign_up_fail_password_not_confirmed() {
		assertThrows(BadRequestException.class, () -> {
			SignUpRequest signUpRequest = mockInvalidSignUpRequest("test3");
			signService.signUp(signUpRequest);
		});
	}

	@Test
	public void sign_in_success() {
		SignInRequest signInRequest = mockSignInRequest("test2");

		given(accountService.findAccount(anyString()))
				.willReturn(Optional.of(Account.of(signInRequest.getUserId(), signInRequest.getPassword())));

		SignInResponse response = signService.signIn(signInRequest);

		assertNotNull(response);
		verify(accountService).findAccount(anyString());
		verify(tokenGranter).grant(anyString(), any(TokenRequest.class));
	}

	@Test
	public void sign_in_fail_not_exist_userId() {
		assertThrows(BusinessValidationException.class, () -> {
			SignInRequest signInRequest = mockSignInRequest("test3");

			given(accountService.findAccount(anyString())).willReturn(Optional.empty());

			signService.signIn(signInRequest);
		});

		verify(accountService).findAccount(anyString());
	}

	private SignUpRequest mockSignUpRequest(String userId) {
		SignUpRequest request = new SignUpRequest();
		request.setUserId(userId);
		request.setPassword("test_pw");
		request.setConfirmPassword("test_pw");

		return request;
	}

	private SignUpRequest mockInvalidSignUpRequest(String userId) {
		SignUpRequest request = mockSignUpRequest(userId);
		request.setConfirmPassword(request.getPassword() + 1);

		 return request;
	}

	private SignInRequest mockSignInRequest(String userId) {
		SignInRequest request = new SignInRequest();
		request.setUserId(userId);
		request.setPassword("test_pw");

		return request;
	}
}
