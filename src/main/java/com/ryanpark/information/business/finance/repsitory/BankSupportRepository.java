/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.repsitory;

import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 기관 별 지원 금액 repository
 */
public interface BankSupportRepository extends JpaRepository<BankSupportEntity, Integer> {
	List<BankSupportEntity> findAllByYear(Integer year);
	List<BankSupportEntity> findAllByBank_name(String name);
	List<BankSupportEntity> findAllByBank_nameAndYearAndMonth(String name, Integer year, Integer month);
}
