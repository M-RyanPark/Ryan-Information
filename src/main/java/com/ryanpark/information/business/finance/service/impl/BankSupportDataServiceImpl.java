/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.service.impl;

import com.ryanpark.information.business.finance.domain.BankSupportData;
import com.ryanpark.information.business.finance.domain.YearlySupportData;
import com.ryanpark.information.business.finance.repsitory.BankRepository;
import com.ryanpark.information.business.finance.repsitory.BankSupportParseData;
import com.ryanpark.information.business.finance.repsitory.BankSupportRepository;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import com.ryanpark.information.business.finance.service.BankSupportDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 기관 지원금 데이터 서비스
 */
@Service
@RequiredArgsConstructor
public class BankSupportDataServiceImpl implements BankSupportDataService {

	final BankRepository bankRepository;
	final BankSupportRepository bankSupportRepository;

	@Transactional
	@Override
	public void createBank(String bankName) {
		if (bankRepository.findByName(bankName).isPresent() == false) {
			bankRepository.save(BankEntity.of(bankName));
		}
	}

	@Transactional
	@Override
	public void createBankSupport(BankSupportParseData data) {
		this.createBankSupport(data.getName(), data.getYear(), data.getMonth(), data.getAmount());
	}

	@Transactional
	@Override
	public void createBankSupport(@NotNull String bankName, @NotNull Integer year, @NotNull Integer month, @NotNull Integer amount) {
		final BankEntity bankEntity = bankRepository.findByName(bankName)
				.orElseGet(() -> BankEntity.of(bankName));

		bankSupportRepository.save(BankSupportEntity.of(bankEntity, year, month, amount));
	}

	@Override
	public List<BankEntity> getAllBankList() {
		return bankRepository.findAll();
	}

	@Override
	public List<YearlySupportData> getYearlySupportData() {
		return findYearlySupportDataOfBank(bankSupportRepository.findAll());
	}

	@Override
	public Optional<BankSupportData> getTopBankSupportDataOfYear(@NotNull Integer year) {
		return findBankSupportDataOfYear(bankSupportRepository.findAllByYear(year))
				.stream()
				.max(Comparator.comparing(BankSupportData::getTotalSupportAmount))
				;
	}

	@Override
	public Optional<BankSupportData> getTotalBankSupportData(@NotNull String bankName) {
		return findBankSupportDataOfYear(bankSupportRepository.findAllByBank_name(bankName))
				.stream()
				.findFirst()
				;
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
