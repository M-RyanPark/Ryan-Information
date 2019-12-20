/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.service.impl;

import com.ryanpark.information.common.domain.AccountUserDetails;
import com.ryanpark.information.common.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-20
 * description : UserDetailService 구현체
 */
@Service
@Slf4j
public class AccountUserDetailService implements UserDetailsService {

	@Autowired
	AccountService accountService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("try login : {}", username);

		return accountService.findAccount(username)
				.map(AccountUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}
}
