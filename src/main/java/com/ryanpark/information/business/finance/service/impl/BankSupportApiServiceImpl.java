/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.service.impl;

import com.ryanpark.information.business.finance.domain.YearlySupportData;
import com.ryanpark.information.business.finance.domain.api.BankListResponse;
import com.ryanpark.information.business.finance.domain.api.BankSupportStatsResponse;
import com.ryanpark.information.business.finance.domain.api.TopSupportBankResponse;
import com.ryanpark.information.business.finance.domain.api.YearlyStatisticsResponse;
import com.ryanpark.information.business.finance.repsitory.BankSupportParseData;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.business.finance.service.BankSupportApiService;
import com.ryanpark.information.business.finance.service.BankSupportDataService;
import com.ryanpark.information.business.finance.service.BankSupportFileReader;
import com.ryanpark.information.framework.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 지원 정보 Api 서비스
 */
@Service
@RequiredArgsConstructor
public class BankSupportApiServiceImpl implements BankSupportApiService {

	final BankSupportDataService bankSupportDataService;
	final BankSupportFileReader bankSupportFileReader;

	@Override
	public void registerBankSupportData(MultipartFile multipartFile) {
		List<BankSupportParseData> registerList = bankSupportFileReader.readBankSupportData(multipartFile);

		registerList.forEach(bankSupportDataService::createBankSupport);
	}

	@Override
	public BankListResponse getBankList() {
		List<BankEntity> resultList = bankSupportDataService.getAllBankList();

		if (resultList.isEmpty()) {
			throw new DataNotFoundException();
		}

		return new BankListResponse(resultList);
	}

	@Override
	public YearlyStatisticsResponse getYearlyStatistic() {
		List<YearlySupportData> resultList = bankSupportDataService.getYearlySupportData()
				.stream()
				.sorted(Comparator.comparing(YearlySupportData::getYear))
				.collect(Collectors.toList())
				;

		if (resultList.isEmpty()) {
			throw new DataNotFoundException();
		}

		return new YearlyStatisticsResponse(resultList);
	}

	@Override
	public TopSupportBankResponse getTopSupportBankOfYear(Integer year) {
		return new TopSupportBankResponse(
				bankSupportDataService.getTopBankSupportDataOfYear(year)
						.orElseThrow(DataNotFoundException::new)
		);
	}

	@Override
	public BankSupportStatsResponse getStatisticOfBank(String bankName) {
		return new BankSupportStatsResponse(
				bankSupportDataService.getTotalBankSupportData(bankName)
						.orElseThrow(DataNotFoundException::new)
		);
	}
}
