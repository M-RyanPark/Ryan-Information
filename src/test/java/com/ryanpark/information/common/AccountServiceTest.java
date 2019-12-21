/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common;

import com.ryanpark.information.business.BusinessCommonTest;
import com.ryanpark.information.common.domain.AccountUserDetails;
import com.ryanpark.information.common.repository.AccountRepository;
import com.ryanpark.information.common.repository.entity.Account;
import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.framework.exception.BaseBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : AccountService 테스트
 */
@Slf4j
public class AccountServiceTest extends BusinessCommonTest {

	@Autowired AccountService accountService;
	@Autowired PasswordEncoder passwordEncoder;
	@MockBean AccountRepository accountRepository;
	@Autowired UserDetailsService userDetailsService;


	private String userId = "userId";
	private String password = "password";
	private String encodedPassword;
	private Account mockAccount;

	@BeforeEach
	public void setUp() {
		encodedPassword = passwordEncoder.encode(password);
		mockAccount = Account.of(userId, encodedPassword);
	}


	@Test
	public void account_create_success() {
		given(accountRepository.save(any(Account.class))).willReturn(mockAccount);
		given(accountRepository.findByUserId(anyString())).willReturn(Optional.empty());

		final Account newAccount = accountService.createAccount(userId, password);
		log.info("new account = {}", newAccount);

		assertNotNull(newAccount);
		assertEquals(userId, newAccount.getUserId());
		assertTrue(passwordEncoder.matches(password, newAccount.getPassword()));
		assertEquals(Account.AccountStatus.ACTIVE, newAccount.getStatus());
		assertTrue(newAccount.isActive());
	}

	@Test
	public void account_create_fail_duplicate() {
		assertThrows(BaseBusinessException.class, () -> {
			given(accountRepository.findByUserId(userId)).willReturn(Optional.of(mockAccount));

			accountService.createAccount(userId, password);
		});
	}

	@Test
	public void account_change_password_success() {
		String changePassword = "change_password";

		given(accountRepository.findByUserId(userId)).willReturn(Optional.of(mockAccount));

		accountService.changePassword(userId, changePassword);

		final Account changed = accountService.findAccount(userId).orElseThrow(RuntimeException::new);

		log.info("changed account = {}", changed);
		assertTrue(passwordEncoder.matches(changePassword, changed.getPassword()));
		assertFalse(passwordEncoder.matches(password, changed.getPassword()), "기존 패스 워드와 hash 값 다름");
	}

	@Test
	public void account_lock_success() {
		given(accountRepository.findByUserId(userId)).willReturn(Optional.of(mockAccount));

		accountService.lockAccount(userId);

		final Account updated = accountService.findAccount(userId).orElseThrow(RuntimeException::new);

		log.info("updated account = {}", updated);

		assertNotNull(updated);
		assertEquals(Account.AccountStatus.LOCKED, updated.getStatus());
	}

	@Test
	public void account_terminate_success() {
		given(accountRepository.findByUserId(userId)).willReturn(Optional.of(mockAccount));
		accountService.terminateAccount(userId);

		final Account updated = accountService.findAccount(userId).orElseThrow(RuntimeException::new);

		log.info("updated account = {}", updated);

		assertNotNull(updated);
		assertEquals(Account.AccountStatus.TERMINATE, updated.getStatus());
	}

	@Test
	public void account_restore_success() {
		Account terminated = Account.of(userId, encodedPassword).changeStatus(Account.AccountStatus.TERMINATE);
		given(accountRepository.findByUserId(userId)).willReturn(Optional.of(terminated));

		accountService.restoreAccount(userId);

		final Account updated = accountService.findAccount(userId).orElseThrow(RuntimeException::new);

		log.info("updated account = {}", updated);

		assertNotNull(updated);
		assertEquals(Account.AccountStatus.ACTIVE, updated.getStatus());
	}

	@Test
	public void account_load_by_username_test() {
		given(accountRepository.findByUserId(userId)).willReturn(Optional.of(mockAccount));

		UserDetails userDetails = userDetailsService.loadUserByUsername(mockAccount.getUserId());

		log.info("UserDetails = {}", userDetails);

		assertNotNull(userDetails);
		assertTrue(AccountUserDetails.class.isAssignableFrom(userDetails.getClass()));
		assertEquals(mockAccount.getUserId(), userDetails.getUsername());
		assertEquals(mockAccount.getPassword(), userDetails.getPassword());
		assertEquals(mockAccount.isActive(), userDetails.isAccountNonExpired());
		assertEquals(mockAccount.isActive(), userDetails.isEnabled());
	}
}
