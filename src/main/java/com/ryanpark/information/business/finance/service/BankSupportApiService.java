/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.service;

import com.ryanpark.information.business.finance.domain.api.BankListResponse;
import com.ryanpark.information.business.finance.domain.api.BankSupportStatsResponse;
import com.ryanpark.information.business.finance.domain.api.TopSupportBankResponse;
import com.ryanpark.information.business.finance.domain.api.YearlyStatisticsResponse;
import com.ryanpark.information.common.domain.api.CommonMessageResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 지원 정보 Api 서비스
 */
public interface BankSupportApiService {
	CommonMessageResponse registerBankSupportData(MultipartFile multipartFile);
	BankListResponse getBankList();
	YearlyStatisticsResponse getYearlyStatistic();
	TopSupportBankResponse getTopSupportBankOfYear(Integer year);
	BankSupportStatsResponse getStatisticOfBank(String bankName);
}
