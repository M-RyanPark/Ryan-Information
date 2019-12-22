/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import com.ryanpark.information.common.domain.AccountUserDetails;
import com.ryanpark.information.common.repository.entity.Account;
import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.common.service.impl.AccountUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : AccountUserDetailService 테스트
 */
@SpringBootTest(classes = AccountUserDetailService.class)
@Slf4j
public class AccountUserDetailServiceTest {

	@Autowired UserDetailsService userDetailsService;
	@MockBean AccountService accountService;

	@Test
	public void userDetailsService_load_by_username_success() {
		String userId = "test";
		Account mockAccount = Account.of(userId, "password");

		given(accountService.findAccount(userId)).willReturn(Optional.of(mockAccount));

		UserDetails userDetails = userDetailsService.loadUserByUsername(mockAccount.getUserId());

		log.info("UserDetails = {}", userDetails);

		assertNotNull(userDetails);
		assertTrue(AccountUserDetails.class.isAssignableFrom(userDetails.getClass()));
		assertEquals(mockAccount.getUserId(), userDetails.getUsername());
		assertEquals(mockAccount.getPassword(), userDetails.getPassword());
		assertEquals(mockAccount.isActive(), userDetails.isAccountNonExpired());
		assertEquals(mockAccount.isActive(), userDetails.isEnabled());
	}

	@Test
	public void userDetailsService_load_by_username_fail_by_username_not_found() {
		assertThrows(UsernameNotFoundException.class, () -> {
			given(accountService.findAccount(anyString())).willReturn(Optional.empty());

			userDetailsService.loadUserByUsername(anyString());
		});

	}
}
