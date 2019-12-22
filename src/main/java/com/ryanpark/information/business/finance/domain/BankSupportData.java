/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.domain;

import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 기관 별 년 간 지원 금액 합계 dataO
 */
@Data
public class BankSupportData {
	private BankEntity bank;
	private List<YearSupportAmount> yearSupportAmountList;

	public BankSupportData(BankEntity bank) {
		this.bank = bank;
		this.yearSupportAmountList = new ArrayList<>();
	}

	public BankSupportData applySupportAmountList(List<BankSupportEntity> bankSupportList) {
		bankSupportList.stream()
				.filter(bankSupport -> this.bank.equals(bankSupport.getBank()))
				.collect(Collectors.groupingBy(BankSupportEntity::getYear, Collectors.toList()))
				.forEach((key, value) ->yearSupportAmountList.add(new YearSupportAmount(key, value)))
		;

		return this;
	}

	public Integer getTotalSupportAmount() {
		return yearSupportAmountList.stream().mapToInt(YearSupportAmount::getTotalAmount).sum();
	}

	public Optional<YearSupportAmount> getMinSupportOfYear() {
		return this.yearSupportAmountList
				.stream()
				.min(Comparator.comparing(YearSupportAmount::getAverage));
	}

	public Optional<YearSupportAmount> getMaxSupportOfYear() {
		return this.yearSupportAmountList
				.stream()
				.max(Comparator.comparing(YearSupportAmount::getAverage));
	}

	@Data
	public static class YearSupportAmount {
		private Integer year;
		private List<MonthSupportAmount> monthSupportAmountList;

		public YearSupportAmount(Integer year, List<BankSupportEntity> bankSupportEntityList) {
			this.year = year;
			monthSupportAmountList = bankSupportEntityList.stream()
					.filter(entity -> entity.getYear().equals(year))
					.map(entity -> MonthSupportAmount.of(entity.getMonth(), entity.getAmount()))
					.collect(Collectors.toList());
		}

		public Integer getTotalAmount() {
			return this.monthSupportAmountList.stream()
					.mapToInt(MonthSupportAmount::getAmount)
					.sum();
		}

		public Integer getAverage() {
			return this.getTotalAmount() / this.monthSupportAmountList.size();
		}
	}

	@Data
	@AllArgsConstructor(staticName = "of")
	public static class MonthSupportAmount {
		private Integer month;
		private Integer amount;
	}
}
