/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import com.ryanpark.information.common.repository.AccountRepository;
import com.ryanpark.information.common.repository.entity.Account;
import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.common.service.impl.AccountServiceImpl;
import com.ryanpark.information.framework.exception.BaseBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : AccountService 테스트
 */
@SpringBootTest(classes = AccountServiceImpl.class)
@Slf4j
public class AccountServiceTest  {

	@Autowired AccountService accountService;
	@MockBean PasswordEncoder passwordEncoder;
	@MockBean AccountRepository accountRepository;


	private String userId = "userId";
	private String password = "password";
	private String encodedPassword = "encoded_password";
	private Account mockAccount;

	@BeforeEach
	public void setUp() {
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
		assertEquals(Account.AccountStatus.ACTIVE, newAccount.getStatus());
		assertTrue(newAccount.isActive());

		verify(accountRepository).findByUserId(anyString());
		verify(accountRepository).save(any(Account.class));
	}

	@Test
	public void account_create_fail_duplicate() {
		assertThrows(BaseBusinessException.class, () -> {
			given(accountRepository.findByUserId(userId)).willReturn(Optional.of(mockAccount));

			accountService.createAccount(userId, password);
		});

		verify(accountRepository).findByUserId(anyString());
	}

	@Test
	public void account_change_password_success() {
		String changePassword = "change_password";

		given(accountRepository.findByUserId(userId)).willReturn(Optional.of(mockAccount));
		given(passwordEncoder.encode(anyString())).willReturn(changePassword);

		accountService.changePassword(userId, changePassword);

		verify(accountRepository).findByUserId(anyString());
		verify(passwordEncoder).encode(anyString());
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
}
