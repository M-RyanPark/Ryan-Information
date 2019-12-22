/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.service;

import com.ryanpark.information.business.finance.domain.BankSupportData;
import com.ryanpark.information.business.finance.domain.YearlySupportData;
import com.ryanpark.information.business.finance.domain.api.BankListResponse;
import com.ryanpark.information.business.finance.domain.api.BankSupportStatsResponse;
import com.ryanpark.information.business.finance.domain.api.TopSupportBankResponse;
import com.ryanpark.information.business.finance.domain.api.YearlyStatisticsResponse;
import com.ryanpark.information.business.finance.repsitory.BankSupportParseData;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import com.ryanpark.information.business.finance.service.BankSupportApiService;
import com.ryanpark.information.business.finance.service.BankSupportDataService;
import com.ryanpark.information.business.finance.service.BankSupportFileReader;
import com.ryanpark.information.business.finance.service.impl.BankSupportApiServiceImpl;
import com.ryanpark.information.framework.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-23
 * description :
 */
@SpringBootTest(classes = BankSupportApiServiceImpl.class)
@Slf4j
public class BankSupportApiTest {
	@Autowired BankSupportApiService bankSupportApiService;
	@MockBean BankSupportDataService bankSupportDataService;
	@MockBean BankSupportFileReader bankSupportFileReader;

	private BankSupportTestData testData = new BankSupportTestData();

	private static final String BANK_NAME_KB = "국민은행";
	private static final String BANK_NAME_SH = "신한은행";
	private static final String BANK_NAME_WR = "우리은행";

	private final BankEntity bankKb = testData.getBankKb();
	private final BankEntity bankSh = testData.getBankSh();
	private final BankEntity bankWr = testData.getBankWr();

	private final List<BankEntity> bankList = testData.getBankList();
	private final List<BankSupportEntity> sampleList = testData.getSampleList();

	private final BankSupportData mockBankSupportData = new BankSupportData(bankKb).applySupportAmountList(sampleList);

	@BeforeEach
	public void setUp() {
		given(bankSupportDataService.getAllBankList()).willReturn(bankList);
	}

	@Test
	public void bank_api_register_success() {
		given(bankSupportFileReader.readBankSupportData(any(MultipartFile.class)))
				.willReturn(sampleList.stream()
						.map(x ->
								BankSupportParseData.of(x.getBank().getName(), x.getYear(), x.getMonth(), x.getAmount()))
						.collect(Collectors.toList())
				);
		doNothing().when(bankSupportDataService).createBankSupport(any());

		bankSupportApiService.registerBankSupportData(Mockito.mock(MultipartFile.class));

		verify(bankSupportFileReader).readBankSupportData(any(MultipartFile.class));
		verify(bankSupportDataService, times(sampleList.size())).createBankSupport(any(BankSupportParseData.class));
	}

	@Test
	public void bank_api_get_all_bankList_success() {
		BankListResponse result = bankSupportApiService.getBankList();
		log.info("result = {}", result);

		assertEquals(bankList.size(), result.getContents().size());
	}

	@Test
	public void bank_api_get_all_bankList_fail_data_not_found() {
		given(bankSupportDataService.getAllBankList()).willReturn(Collections.emptyList());

		expect_DataNotfoundException(() -> bankSupportApiService.getBankList());
	}

	@Test
	public void bank_api_get_yearly_data_success() {
		given(bankSupportDataService.getYearlySupportData())
				.willReturn(
						findYearlySupportDataOfBank(sampleList.stream().filter(x -> x.getYear() == 2010).collect(Collectors.toList())
						));

		YearlyStatisticsResponse result = bankSupportApiService.getYearlyStatistic();
		log.info("result = {}", result);

		assertEquals(1, result.getContents().size());
		assertEquals(2, result.getContents().get(0).getBankSupportAmountList().size());
	}

	@Test
	public void bank_api_get_yearly_data_fail_data_not_found() {
		given(bankSupportDataService.getYearlySupportData()).willReturn(Collections.emptyList());

		expect_DataNotfoundException(() -> bankSupportApiService.getYearlyStatistic());
	}

	@Test
	public void bank_api_get_top_support_bank_success() {
		given(bankSupportDataService.getTopBankSupportDataOfYear(anyInt()))
				.willReturn(Optional.ofNullable(mockBankSupportData));

		TopSupportBankResponse result = bankSupportApiService.getTopSupportBankOfYear(2000);

		log.info("result = {}", result);

		assertEquals(BANK_NAME_KB, result.getBank().getName());
		assertEquals(2010, result.getYear());
		assertEquals(14000, result.getTotalAmount());
	}

	@Test
	public void bank_api_get_top_support_fail_data_not_found() {
		given(bankSupportDataService.getTopBankSupportDataOfYear(anyInt())).willReturn(Optional.empty());

		expect_DataNotfoundException(() -> bankSupportApiService.getTopSupportBankOfYear(2000));
	}

	@Test
	public void bank_api_get_stats_bank_success() {
		given(bankSupportDataService.getTotalBankSupportData(anyString()))
				.willReturn(Optional.ofNullable(mockBankSupportData));

		BankSupportStatsResponse result = bankSupportApiService.getStatisticOfBank(BANK_NAME_KB);

		log.info("result = {}", result);

		assertEquals(BANK_NAME_KB, result.getBank().getName());
		assertEquals(mockBankSupportData.getMaxSupportOfYear().get().getYear(), result.getMaxSupport().getYear());
		assertEquals(mockBankSupportData.getMaxSupportOfYear().get().getAverage(), result.getMaxSupport().getAmount());
		assertEquals(mockBankSupportData.getMinSupportOfYear().get().getYear(), result.getMinSupport().getYear());
		assertEquals(mockBankSupportData.getMinSupportOfYear().get().getAverage(), result.getMinSupport().getAmount());
	}

	@Test
	public void bank_api_get_stats_fail_data_not_found() {
		given(bankSupportDataService.getTotalBankSupportData(anyString())).willReturn(Optional.empty());

		expect_DataNotfoundException(() -> bankSupportApiService.getStatisticOfBank(BANK_NAME_KB));
	}

	private void expect_DataNotfoundException(Executable executable) {
		Exception exception = assertThrows(DataNotFoundException.class, executable);

		assertTrue(exception.getClass().isAssignableFrom(DataNotFoundException.class));
	}

	private List<YearlySupportData> findYearlySupportDataOfBank(List<BankSupportEntity> bankSupportList) {
		return bankSupportList
				.stream()
				.collect(Collectors.groupingBy(BankSupportEntity::getYear, Collectors.toList()))
				.entrySet()
				.stream()
				.map(x -> new YearlySupportData(x.getKey()).applySupportAmountList(x.getValue()))
				.collect(Collectors.toList())
				;
	}

	private List<BankSupportData> findBankSupportDataOfYear(List<BankSupportEntity> bankSupportList) {
		return bankSupportList
				.stream()
				.collect(Collectors.groupingBy(BankSupportEntity::getBank, Collectors.toList()))
				.entrySet()
				.stream()
				.map(x -> new BankSupportData(x.getKey()).applySupportAmountList(x.getValue()))
				.collect(Collectors.toList())
				;
	}
}
