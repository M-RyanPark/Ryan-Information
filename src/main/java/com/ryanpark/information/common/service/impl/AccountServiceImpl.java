/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.service.impl;

import com.ryanpark.information.common.repository.AccountRepository;
import com.ryanpark.information.common.repository.entity.Account;
import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.framework.exception.BusinessValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : 계정 관리 서비스 Repository 구현체
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public Optional<Account> findAccount(Long userNo) {
		return accountRepository.findByUserNo(userNo);
	}

	@Override
	public Optional<Account> findAccount(String userId) {
		return accountRepository.findByUserId(userId);
	}

	@Transactional
	@Override
	public Account createAccount(String userId, String password) {
		if (this.findAccount(userId).isPresent()) {
			log.warn("이미 존재하는 계정 : {}", userId);
			throw BusinessValidationException.of("이미 존재하는 아이디 입니다.");
		}
		return accountRepository.save(Account.of(userId, passwordEncoder.encode(password)));
	}

	@Transactional
	@Override
	public void changePassword(String userId, String password) {
		this.findAccount(userId)
				.orElseThrow(this::accountNotFoundException)
				.changePassword(hashPassword(password))
				;
	}

	@Override
	public boolean validatePassword(Account account, String password) {
		return passwordEncoder.matches(password, account.getPassword());
	}

	@Transactional
	@Override
	public void lockAccount(String userId) {
		this.changeAccountStatus(userId, Account.AccountStatus.LOCKED);
	}

	@Transactional
	@Override
	public void restoreAccount(String userId) {
		this.changeAccountStatus(userId, Account.AccountStatus.ACTIVE);
	}

	@Transactional
	@Override
	public void terminateAccount(String userId) {
		this.changeAccountStatus(userId, Account.AccountStatus.TERMINATE);
	}

	private void changeAccountStatus(String userId, Account.AccountStatus status) {
		this.findAccount(userId)
				.orElseThrow(this::accountNotFoundException)
				.changeStatus(status)
				;
	}

	private String hashPassword(String originalPassword) {
		return passwordEncoder.encode(originalPassword);
	}

	private BusinessValidationException accountNotFoundException() {
		return BusinessValidationException.of("계정이 존재하지 않습니다.");
	}
}
