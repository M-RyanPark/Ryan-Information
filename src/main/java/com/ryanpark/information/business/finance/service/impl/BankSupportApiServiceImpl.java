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
import com.ryanpark.information.common.domain.api.CommonMessageResponse;
import com.ryanpark.information.framework.exception.BusinessValidationException;
import com.ryanpark.information.framework.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 지원 정보 Api 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankSupportApiServiceImpl implements BankSupportApiService {

	private static final String REGISTER_COMPLETE_MESSAGE_FORMAT = "등록 작업을 완료하였습니다. (전체 : %d / 실패 : %d)";
	private static final int MAX_THREAD_COUNT = 8;
	private static final int AWAIT_TIME_SECONDS = 10;

	final BankSupportDataService bankSupportDataService;
	final BankSupportFileReader bankSupportFileReader;

	@Override
	@Transactional
	public CommonMessageResponse registerBankSupportData(MultipartFile multipartFile) {
		List<BankSupportParseData> registerList = bankSupportFileReader.readBankSupportData(multipartFile);

		if (CollectionUtils.isEmpty(registerList)) {
			throw BusinessValidationException.of("등록할 대상이 없습니다.");
		}

		List<BankSupportRegisterResult> resultList = registerBankSupportList(registerList);
		long failCount = resultList.stream().filter(x -> x.isResult() == false).count();

		String resultMessage = String.format(REGISTER_COMPLETE_MESSAGE_FORMAT, registerList.size(), failCount);
		log.info(resultMessage);

		return CommonMessageResponse.of(resultMessage);
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

	private List<BankSupportRegisterResult> registerBankSupportList(List<BankSupportParseData> registerList) {
		log.info("금융 지원 자료 등록 시작");
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

		List<BankSupportRegisterResult> targetList = registerList.stream()
				.map(item -> CompletableFuture.supplyAsync(() -> {
					try {
						bankSupportDataService.createBankSupport(item);

						return new BankSupportRegisterResult(true);
					} catch (Exception e) {
						log.warn("금융 지원 정보 등록 실패 : {}", e.getMessage());
						return new BankSupportRegisterResult(false);
					}
				}, executorService))
				.collect(Collectors.toList())
				.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList())
				;

		executorService.shutdown();

		try {
			executorService.awaitTermination(AWAIT_TIME_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.warn("금융 지원 정보 등록 작업 실패", e);
		}

		log.info("금융 지원 자료 등록 종료");

		return targetList;
	}

	@Data
	@AllArgsConstructor
	static class BankSupportRegisterResult {
		private boolean result;
	}
}
