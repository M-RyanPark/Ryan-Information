/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.repository;

import com.ryanpark.information.common.repository.AccountRepository;
import com.ryanpark.information.common.repository.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : Account Repository 테스트
 */
@Slf4j
public class AccountRepositoryTest extends CommonRepositoryTest {

	@Autowired
	AccountRepository accountRepository;

	@Test
	public void account_test_create_and_find_success() {
		final Account account = Account.of("test1", "test1");
		log.info("account = {}", account);

		final Account saved = accountRepository.save(account);
		log.info("saved = {}", saved);

		final Account findAccount = accountRepository.findByUserId("test1").orElse(null);

		assertNotNull(saved);
		assertEquals(account, saved);
		assertEquals(account, findAccount);
		assertEquals(Account.AccountStatus.ACTIVE, saved.getStatus());
		assertTrue(saved.isActive());
	}

	@Test
	public void account_test_create_fail_dup_user_id() {
		expect_ConstraintViolationException(() -> {
			final Account account1 = Account.of("test1", "test1");
			final Account account2 = Account.of("test1", "test1");

			accountRepository.save(account1);
			log.info("account1 = {}", account1);
			entityManager.flush();	// flush가 호출 되기 전까진 sql 실행 이전이므로 sql Excpetion을 확인 하기 위해서는 flush를 호출하여 강제로 sql 실행 상태로 만들어야 한다

			accountRepository.save(account2);
			log.info("account2 = {}", account2);
			entityManager.flush();
		});
	}

	@Test
	public void account_test_create_fail_long_user_id() {
		expect_ConstraintViolationException(() -> {
			final Account account = Account.of("test12222222222222222222222222222222222222222222222222222222222", "test1");

			accountRepository.save(account);
			entityManager.flush();
		});
	}

	@Test
	public void account_test_create_fail_null_validation() {
		expect_ConstraintViolationException(() -> {
			final Account account = Account.of("test12", "test12");
			account.changeStatus(null);

			accountRepository.save(account);
			entityManager.flush();
		});
	}

	private void expect_ConstraintViolationException(Executable executable) {
		Exception exception = assertThrows(DataIntegrityViolationException.class, executable);

		assertTrue(exception.getClass().isAssignableFrom(DataIntegrityViolationException.class));
	}
}
