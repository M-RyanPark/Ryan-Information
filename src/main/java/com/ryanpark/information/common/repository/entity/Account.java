/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.repository.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : 사용자 정보를 저장할 Entity
 */
@Entity
@Table(name = "tb_account")
@Getter
@ToString
@NoArgsConstructor
public class Account extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userNo;

	@Column(length = 30, nullable = false, unique = true)
	private String userId;

	@Column(length = 200, nullable = false)
	private String password;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountStatus status;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@Column(name = "authority")
	private Set<AccountAuthority> authorities;

	private Account(String userId, String password) {
		this.userId = userId;
		this.password = password;
		this.active = true;
		this.status = AccountStatus.ACTIVE;
		this.authorities = new HashSet<>();
		this.authorities.add(AccountAuthority.USER);
	}

	public static Account of(String userId, String password) {
		return new Account(userId, password);
	}

	public Account changeStatus(AccountStatus status) {
		this.status = status;
		return this;
	}

	public Account changePassword(String password) {
		this.password = password;
		return this;
	}

	public enum AccountAuthority implements GrantedAuthority {
		ADMIN, USER;

		@Override
		public String getAuthority() {
			return this.name();
		}
	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum AccountStatus {
		ACTIVE("활성")
		,
		LOCKED("잠김")
		,
		TERMINATE("탈퇴")
		;

		final String desc;
	}
}
