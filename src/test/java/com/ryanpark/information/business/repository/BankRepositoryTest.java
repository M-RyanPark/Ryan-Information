/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.repository;

import com.ryanpark.information.business.finance.repsitory.BankRepository;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.common.repository.CommonRepositoryTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : BankRepository 테스트
 */
@Slf4j
public class BankRepositoryTest extends CommonRepositoryTest {

	private static final String BANK_NAME_KB = "국민은행";
	private static final String BANK_NAME_SH = "신한은행";
	private static final String BANK_NAME_WR = "우리은행";

	@Autowired
	BankRepository bankRepository;

	@Test
	public void bank_save_and_find_success() {
		final BankEntity saved = bankRepository.save(BankEntity.of(BANK_NAME_KB));
		log.info("saved = {}", saved);

		final BankEntity findBank = bankRepository.findByName(BANK_NAME_KB).orElse(null);

		assertNotNull(saved);
		assertEquals(findBank, saved);
	}

	@Test
	public void bank_save_fail_dup_bank_name() {
		expect_ConstraintViolationException(() -> {
			final BankEntity saved1 = bankRepository.save(BankEntity.of(BANK_NAME_KB));
			log.info("saved1 = {}", saved1);
			entityManager.flush();

			final BankEntity saved2 = bankRepository.save(BankEntity.of(BANK_NAME_KB));
			log.info("saved2 = {}", saved2);
			entityManager.flush();
		});
	}

	@Test
	public void bank_save_and_find_all_success() {
		bankRepository.save(BankEntity.of(BANK_NAME_KB));
		bankRepository.save(BankEntity.of(BANK_NAME_SH));
		bankRepository.save(BankEntity.of(BANK_NAME_WR));

		List<BankEntity> bankList = bankRepository.findAll();
		log.info("bankList = {}", bankList);

		assertNotNull(bankList);
		assertFalse(CollectionUtils.isEmpty(bankList));
		assertEquals(3, bankList.size());
	}
}
