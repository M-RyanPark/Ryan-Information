/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.service;

import com.ryanpark.information.business.finance.domain.BankSupportData;
import com.ryanpark.information.business.finance.domain.YearlySupportData;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 기관 지원금 데이터 서비스
 */
public interface BankSupportDataService {
	void createBank(String name);
	void createBankSupport(String bankName, Integer year, Integer month, Integer amount);
	List<BankEntity> getAllBankList();
	List<YearlySupportData> getYearlySupportData();
	Optional<BankSupportData> getTopBankSupportDataOfYear(Integer year);
	Optional<BankSupportData> getTotalBankSupportData(String bankName);
}
