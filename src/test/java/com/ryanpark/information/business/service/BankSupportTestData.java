/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.service;

import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-23
 * description :
 */
@Getter
public class BankSupportTestData {
	public static final String BANK_NAME_KB = "국민은행";
	public static final String BANK_NAME_SH = "신한은행";
	public static final String BANK_NAME_WR = "우리은행";

	private final BankEntity bankKb = new BankEntity(1, BANK_NAME_KB);
	private final BankEntity bankSh = new BankEntity(2, BANK_NAME_SH);
	private final BankEntity bankWr = new BankEntity(3, BANK_NAME_WR);

	private final List<BankEntity> bankList = Arrays.asList(bankKb, bankSh, bankWr);
	private final List<BankSupportEntity> sampleList = sampleData();

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
