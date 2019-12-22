/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.service;

import com.ryanpark.information.business.finance.repsitory.BankRepository;
import com.ryanpark.information.business.finance.repsitory.BankSupportRepository;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import com.ryanpark.information.business.finance.service.BankSupportDataService;
import com.ryanpark.information.business.finance.service.impl.BankSupportDataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : BankSupportDataService Entity 생성 테스트
 */
@SpringBootTest(classes = BankSupportDataServiceImpl.class)
@Slf4j
public class BankSupportDataServiceCreateTest {

	@Autowired BankSupportDataService bankSupportDataService;
	@MockBean BankRepository bankRepository;
	@MockBean BankSupportRepository bankSupportRepository;

	private static final String BANK_NAME_KB = "국민은행";
	private static final BankEntity bankKb = new BankEntity(1, BANK_NAME_KB);

	@Test
	public void bank_create_ignore_already_exist_bank() {
		given(bankRepository.findByName(anyString())).willReturn(Optional.of(bankKb));

		bankSupportDataService.createBank(BANK_NAME_KB);

		verify(bankRepository).findByName(anyString());
		verify(bankRepository, never()).save(any(BankEntity.class));
	}

	@Test
	public void bank_create_success() {
		given(bankRepository.findByName(anyString())).willReturn(Optional.empty());

		bankSupportDataService.createBank(BANK_NAME_KB);

		verify(bankRepository).findByName(anyString());
		verify(bankRepository).save(any(BankEntity.class));
	}

	@Test
	public void bank_support_create_with_already_exist_bank() {
		given(bankRepository.findByName(anyString())).willReturn(Optional.of(bankKb));

		bankSupportDataService.createBankSupport(BANK_NAME_KB, 2010, 8, 1000);

		verify(bankSupportRepository).save(any(BankSupportEntity.class));
	}

	@Test
	public void bank_support_create_with_new_bank() {
		given(bankRepository.findByName(anyString())).willReturn(Optional.empty());

		bankSupportDataService.createBankSupport(BANK_NAME_KB, 2010, 8, 1000);

		verify(bankSupportRepository).save(any(BankSupportEntity.class));
	}
}
