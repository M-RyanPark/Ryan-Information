/*
 * Copyright (c) 2020 Ryan Information Test
 */

package com.ryanpark.information.framework.oauth;

import com.ryanpark.information.common.domain.AccountUserDetails;
import com.ryanpark.information.common.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2020-01-06
 * description : Account.userId 대신 userNo를 jwtToken의 claim으로 구성하기 위한 UserAuthenticationConverter
 */
@RequiredArgsConstructor(staticName = "of")
public class AccountUserNoAuthenticationConverter implements UserAuthenticationConverter {

	final UserDetailsService userDetailsService;
	final AccountService accountService;
	public final static String USER_NO = "user_no";

	@Override
	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		AccountUserDetails accountUserDetails = (AccountUserDetails) authentication.getPrincipal();
		response.put(USER_NO, String.valueOf(accountUserDetails.getUserNo()));

		return response;
	}

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		if (map.containsKey(USER_NO)) {
			Long userNo = Long.valueOf((String) map.get(USER_NO));

			return accountService.findAccount(userNo)
					.map(account -> userDetailsService.loadUserByUsername(account.getUserId()))
					.map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities()))
					.orElse(null)
					;
		}

		return null;
	}
}
