/*
 * Copyright (c) 2020 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import com.ryanpark.information.common.domain.AccountUserDetails;
import com.ryanpark.information.common.repository.entity.Account;
import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.framework.oauth.AccountUserNoAuthenticationConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2020-01-07
 * description : AccountUserNoAuthenticationConverter 테스트
 */
@Slf4j
public class AccountUserNoAuthenticationConverterTest {

	private static final long TEST_USER_NO = 1L;

	AccountService accountService = mock(AccountService.class);
	UserDetailsService userDetailsService = mock(UserDetailsService.class);
	AccountUserNoAuthenticationConverter authenticationConverter = AccountUserNoAuthenticationConverter.of(userDetailsService, accountService);

	@Test
	public void success_convert_user_authentication() {
		OAuth2Authentication authentication = Mockito.mock(OAuth2Authentication.class);
		given(authentication.getPrincipal())
				.willReturn(mockAccountDetail(TEST_USER_NO));

		Map<String, ?> response = authenticationConverter.convertUserAuthentication(authentication);

		assertEquals(String.valueOf(TEST_USER_NO), response.get(AccountUserNoAuthenticationConverter.USER_NO));
	}

	@Test
	public void success_extract_authentication() {
		Map<String, ?> param = mockParam(TEST_USER_NO);
		AccountUserDetails mockUserDetail = mockAccountDetail(TEST_USER_NO);
		given(accountService.findAccount(anyLong()))
				.willReturn(Optional.of(mockAccount(TEST_USER_NO)));
		given(userDetailsService.loadUserByUsername(anyString()))
				.willReturn(mockUserDetail);

		Authentication authentication = authenticationConverter.extractAuthentication(param);

		assertEquals(mockUserDetail, authentication.getPrincipal());
	}

	@Test
	public void extract_authentication_expect_null_when_param_not_include_user_no() {
		Authentication authentication = authenticationConverter.extractAuthentication(Collections.emptyMap());

		assertNull(authentication);
	}

	@Test
	public void extract_authentication_expect_null_when_cannot_find_account() {
		Map<String, ?> param = mockParam(TEST_USER_NO);
		given(accountService.findAccount(anyLong()))
				.willReturn(Optional.empty());

		Authentication authentication = authenticationConverter.extractAuthentication(param);

		assertNull(authentication);
	}

	private AccountUserDetails mockAccountDetail(Long userNo) {
		return new AccountUserDetails(mockAccount(userNo));
	}

	private Account mockAccount(Long userNo) {
		Account account = Account.of("test1", "test1");
		account.setUserNo(userNo);

		return account;
	}

	private Map<String, ?> mockParam(Long userNo) {
		return Collections.singletonMap(AccountUserNoAuthenticationConverter.USER_NO, String.valueOf(userNo));
	}
}
