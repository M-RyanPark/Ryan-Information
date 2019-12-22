/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.repsitory;

import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 기관 repository
 */
public interface BankRepository extends JpaRepository<BankEntity, Integer> {
	Optional<BankEntity> findByName(String name);
}
