/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.service;

import com.ryanpark.information.common.repository.entity.Account;

import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : 계정 관리 서비스
 */
public interface AccountService {
	Optional<Account> findAccount(Long userNo);
	Optional<Account> findAccount(String userId);
	Account createAccount(String userId, String password);
	void changePassword(String userId, String password);
	boolean validatePassword(Account account, String password);
	void lockAccount(String userId);
	void restoreAccount(String userId);
	void terminateAccount(String userId);
}
