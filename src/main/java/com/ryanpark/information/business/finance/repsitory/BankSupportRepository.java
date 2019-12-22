/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.repsitory;

import com.ryanpark.information.business.finance.repsitory.entity.BankSupport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 기관 별 지원 금액 repository
 */
public interface BankSupportRepository extends JpaRepository<BankSupport, Integer> {
	List<BankSupport> findAllByYear(Integer year);
	List<BankSupport> findAllByBank_name(String name);
	List<BankSupport> findAllByBank_nameAndYearAndMonth(String name, Integer year, Integer month);
}
