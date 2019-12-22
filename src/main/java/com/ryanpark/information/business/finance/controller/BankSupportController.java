/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.controller;

import com.ryanpark.information.business.finance.domain.api.BankListResponse;
import com.ryanpark.information.business.finance.domain.api.BankSupportStatsResponse;
import com.ryanpark.information.business.finance.domain.api.TopSupportBankResponse;
import com.ryanpark.information.business.finance.domain.api.YearlyStatisticsResponse;
import com.ryanpark.information.business.finance.service.BankSupportApiService;
import com.ryanpark.information.common.domain.api.CommonMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 지원 Api
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
public class BankSupportController {

	final BankSupportApiService bankSupportApiService;

	@PostMapping(value = "/api/bank/support/data")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonMessageResponse getBankList(@RequestParam("file")MultipartFile multipartFile) {
		bankSupportApiService.registerBankSupportData(multipartFile);

		return CommonMessageResponse.of("정상적으로 등록되었습니다.");
	}

	@GetMapping("/api/bank/list")
	public BankListResponse getBankList() {
		return bankSupportApiService.getBankList();
	}

	@GetMapping("/api/bank/support/year/list")
	public YearlyStatisticsResponse getYearlyStatistic() {
		return bankSupportApiService.getYearlyStatistic();
	}

	@GetMapping("/api/bank/support/year/top/{year}")
	public TopSupportBankResponse getTopSupportBankOfYear(@PathVariable("year") Integer year) {
		return bankSupportApiService.getTopSupportBankOfYear(year);
	}

	@GetMapping("/api/bank/support/stats/bank")
	public BankSupportStatsResponse getStatsOfBank(@RequestParam("name") String name) {
		return bankSupportApiService.getStatisticOfBank(name);
	}
}
