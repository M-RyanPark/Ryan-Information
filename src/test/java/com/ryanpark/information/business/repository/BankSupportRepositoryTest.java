/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.repository;

import com.ryanpark.information.business.finance.repsitory.BankSupportRepository;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import com.ryanpark.information.common.repository.CommonRepositoryTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description :
 */
@Slf4j
public class BankSupportRepositoryTest extends CommonRepositoryTest {

	private static final String BANK_NAME_KB = "국민은행";
	private static final String BANK_NAME_SH = "신한은행";
	private static final String BANK_NAME_WR = "우리은행";

	@Autowired
	BankSupportRepository bankSupportRepository;

	Map<String, BankEntity> sampleBank = sampleMap();
	List<BankSupportEntity> sampleBankSupportList = sampleSupportList();

	@BeforeEach
	public void setUp() {
		bankSupportRepository.deleteAll();
		this.sampleBankSupportList.forEach(bankSupportRepository::save);
	}

	@Test
	public void bank_support_save_success() {
		final BankSupportEntity saved = bankSupportRepository.save(sampleBankSupport(BANK_NAME_WR, 2017, 1));
		log.info("saved = {}", saved);

		assertNotNull(saved);
	}

	@Test
	public void bank_support_save_fail_dup_unique_index() {
		expect_ConstraintViolationException(() -> {
			final BankSupportEntity saved1 = bankSupportRepository.save(sampleBankSupport(BANK_NAME_WR, 2015, 1));
			log.info("saved1 = {}", saved1);
			entityManager.flush();

			final BankSupportEntity saved2 = bankSupportRepository.save(sampleBankSupport(BANK_NAME_WR, 2015, 1));
			log.info("saved2 = {}", saved2);
			entityManager.flush();
		});
	}

	@Test
	public void bank_support_find_all_success() {
		final List<BankSupportEntity> list = bankSupportRepository.findAll();

		assertEquals(sampleBankSupportList.size(), list.size());
	}

	@Test
	public void bank_support_find_by_year_success() {
		int year = 2010;

		final List<BankSupportEntity> list = bankSupportRepository.findAllByYear(2010);

		assertEquals(sampleBankSupportList.stream().filter(x -> x.getYear() == year).count(), list.size());
	}

	@Test
	public void bank_support_find_by_bank_name_success() {
		String bankName = BANK_NAME_SH;

		final List<BankSupportEntity> list = bankSupportRepository.findAllByBank_name(bankName);

		assertEquals(sampleBankSupportList.stream().filter(x -> x.getBank().getName().equals(bankName)).count(), list.size());
	}

	@Test
	public void bank_support_find_by_bank_name_and_year_and_month_success() {
		String bankName = BANK_NAME_SH;
		int year = 2011;
		int month = 3;

		final List<BankSupportEntity> list = bankSupportRepository.findAllByBank_nameAndYearAndMonth(bankName, year, month);

		assertEquals(sampleBankSupportList
						.stream()
						.filter(x -> x.getBank().getName().equals(bankName))
						.filter(x -> x.getYear() == year)
						.filter(x -> x.getMonth() == month)
						.count()
				, list.size());
	}

	private List<BankSupportEntity> sampleSupportList() {
		return Arrays.asList(
				sampleBankSupport(BANK_NAME_KB, 2010, 1)
				, sampleBankSupport(BANK_NAME_KB, 2010, 2)
				, sampleBankSupport(BANK_NAME_KB, 2011, 3)
				, sampleBankSupport(BANK_NAME_KB, 2012, 4)
				, sampleBankSupport(BANK_NAME_KB, 2012, 1)
				, sampleBankSupport(BANK_NAME_SH, 2010, 1)
				, sampleBankSupport(BANK_NAME_SH, 2010, 2)
				, sampleBankSupport(BANK_NAME_WR, 2012, 1)
		);
	}

	private Map<String, BankEntity> sampleMap() {
		Map<String, BankEntity> sampleMap = new HashMap<>();

		sampleMap.put(BANK_NAME_KB, BankEntity.of(BANK_NAME_KB));
		sampleMap.put(BANK_NAME_SH, BankEntity.of(BANK_NAME_SH));
		sampleMap.put(BANK_NAME_WR, BankEntity.of(BANK_NAME_WR));

		return sampleMap;
	}

	private BankSupportEntity sampleBankSupport(String bankName, int year, int month) {
		BankEntity bank = new BankEntity();
		bank.setName(bankName);
		bank.setCode(1);

		BankSupportEntity bankSupport = new BankSupportEntity();
		bankSupport.setBank(sampleBank.get(bankName));
		bankSupport.setYear(year);
		bankSupport.setMonth(month);
		bankSupport.setAmount(1000);

		return bankSupport;
	}
}
