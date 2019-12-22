/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.service;

import com.ryanpark.information.business.finance.domain.BankSupportData;
import com.ryanpark.information.business.finance.domain.YearlySupportData;
import com.ryanpark.information.business.finance.repsitory.BankRepository;
import com.ryanpark.information.business.finance.repsitory.BankSupportRepository;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import com.ryanpark.information.business.finance.service.BankSupportDataService;
import com.ryanpark.information.business.finance.service.impl.BankSupportDataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : BankSupportDataService 조회 테스트
 */
@SpringBootTest(classes = BankSupportDataServiceImpl.class)
@Slf4j
public class BankSupportDataServiceRetrieveTest {

	@Autowired BankSupportDataService bankSupportDataService;
	@MockBean BankRepository bankRepository;
	@MockBean BankSupportRepository bankSupportRepository;

	private static final String BANK_NAME_KB = "국민은행";
	private static final String BANK_NAME_SH = "신한은행";
	private static final String BANK_NAME_WR = "우리은행";

	private static final BankEntity bankKb = new BankEntity(1, BANK_NAME_KB);
	private static final BankEntity bankSh = new BankEntity(2, BANK_NAME_SH);
	private static final BankEntity bankWr = new BankEntity(3, BANK_NAME_WR);

	private final List<BankEntity> bankList = Arrays.asList(bankKb, bankSh, bankWr);
	private final List<BankSupportEntity> sampleList = sampleData();

	@BeforeEach
	public void setUp() {
		given(bankRepository.findAll()).willReturn(bankList);
		given(bankSupportRepository.findAll()).willReturn(sampleList);
	}

	@Test
	public void bank_support_data_find_all_success() {
		final List<BankEntity> list = bankSupportDataService.getAllBankList();

		assertEquals(bankList.size(), list.size());
	}

	@Test
	public void bank_support_data_find_yearly_bank_data() {
		final List<YearlySupportData> list = bankSupportDataService.getYearlySupportData();
		log.info("list = {}", list);

		long expectedListCount = sampleList.stream()
				.mapToInt(BankSupportEntity::getYear)
				.distinct()
				.count()
				;
		assertEquals(expectedListCount, list.size());

		sampleList.stream()
				.mapToInt(BankSupportEntity::getYear)
				.distinct()
				.forEach(year -> {
					int expectedSum = sampleList.stream()
							.filter(x -> x.getYear().equals(year))
							.mapToInt(BankSupportEntity::getAmount)
							.sum();
					int resultSum = list.stream()
							.filter(yearlySupportData -> yearlySupportData.getYear().equals(year))
							.findFirst()
							.map(YearlySupportData::getTotalAmount)
							.orElse(0)
							;

					log.info("diff sum : {} = {}", expectedSum, resultSum);

					assertEquals(expectedSum, resultSum);
				})
		;
	}

	@Test
	public void bank_support_data_find_top_bank_of_year() {
		int year = 2011;
		List<BankSupportEntity> listOfYear = sampleList.stream()
				.filter(x -> x.getYear().equals(year))
				.collect(Collectors.toList());

		given(bankSupportRepository.findAllByYear(year))
				.willReturn(listOfYear);

		final BankSupportData result = bankSupportDataService.getTopBankSupportDataOfYear(year)
				.orElseThrow(RuntimeException::new);
		log.info("result = {}", result);

		assertNotNull(result);
		assertEquals(bankWr, result.getBank());
	}

	@Test
	public void bank_support_data_bank_data_of_year() {
		List<BankSupportEntity> listOfBank = sampleList.stream()
				.filter(x -> x.getBank().getName().equals(BANK_NAME_SH))
				.collect(Collectors.toList());

		given(bankSupportRepository.findAllByBank_name(BANK_NAME_SH))
				.willReturn(listOfBank);

		final BankSupportData result = bankSupportDataService.getTotalBankSupportData(BANK_NAME_SH)
				.orElseThrow(RuntimeException::new);

		log.info("result = {}", result);

		assertNotNull(result);
		assertEquals(bankSh, result.getBank());
		assertEquals(2010, result.getMinSupportOfYear().get().getYear());
		assertEquals(2011, result.getMaxSupportOfYear().get().getYear());
	}

	private List<BankSupportEntity> sampleData() {
		List<BankSupportEntity> entityList = new ArrayList<>();

		IntStream.range(1,5)
				.forEach(i -> {
					entityList.add(BankSupportEntity.of(bankKb, 2010, i, 1000));
				});

		IntStream.range(1,3)
				.forEach(i -> {
					entityList.add(BankSupportEntity.of(bankKb, 2011, i, 1000));
				});

		IntStream.range(1,9)
				.forEach(i -> {
					entityList.add(BankSupportEntity.of(bankKb, 2012, i, 1000));
				});


		IntStream.range(1,5)
				.forEach(i -> {
					entityList.add(BankSupportEntity.of(bankSh, 2010, i, 300));
				});

		IntStream.range(1,3)
				.forEach(i -> {
					entityList.add(BankSupportEntity.of(bankSh, 2011, i, 1500));
				});

		IntStream.range(1,5)
				.forEach(i -> {
					entityList.add(BankSupportEntity.of(bankWr, 2011, i, 1200));
				});

		return entityList;
	}
}
