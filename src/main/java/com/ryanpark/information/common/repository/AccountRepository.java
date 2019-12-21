/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.repository;

import com.ryanpark.information.common.repository.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description :
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByUserNo(Long userNo);
	Optional<Account> findByUserId(String userId);
}
