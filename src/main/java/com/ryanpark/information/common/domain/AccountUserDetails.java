/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.domain;

import com.ryanpark.information.common.repository.entity.Account;
import com.ryanpark.information.framework.exception.AuthenticationException;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-20
 * description : Account Entity 를 포함하는 로그인 UserDetails
 */
@ToString
public class AccountUserDetails implements UserDetails {

	private Account account;

	public AccountUserDetails(Account account) {
		this.account = Optional.ofNullable(account)
				.orElseThrow(() -> AuthenticationException.of("계정 정보가 존재 하지 않습니다."));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return account.getAuthorities();
	}

	@Override
	public String getPassword() {
		return account.getPassword();
	}

	@Override
	public String getUsername() {
		return account.getUserId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return account.isActive();
	}

	@Override
	public boolean isAccountNonLocked() {
		return account.getStatus() != Account.AccountStatus.LOCKED;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return account.isActive();
	}

	@Override
	public boolean isEnabled() {
		return account.isActive();
	}

	public Long getUserNo() {
		return this.account.getUserNo();
	}
}
