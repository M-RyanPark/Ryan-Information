/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.domain;

import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 년단위 금융 기관 별 지원 금액 data
 */
@Data
@JsonPropertyOrder({"year", "totalAmount", "detailList"})
public class YearlySupportData implements Serializable {
	private Integer year;
	@JsonProperty("detailList")
	private List<BankSupportAmount> bankSupportAmountList;

	public YearlySupportData(Integer year) {
		this.year = year;
		this.bankSupportAmountList = new ArrayList<>();
	}

	public YearlySupportData applySupportAmountList(List<BankSupportEntity> bankSupportList) {
		bankSupportList.stream()
				.filter(bankSupport -> this.year.equals(bankSupport.getYear()))
				.collect(Collectors.groupingBy(BankSupportEntity::getBank, Collectors.summingInt(BankSupportEntity::getAmount)))
				.forEach((key, value) -> bankSupportAmountList.add(new BankSupportAmount(BankResponseItem.of(key), value)))
		;

		return this;
	}

	public Integer getTotalAmount() {
		return this.bankSupportAmountList.stream().mapToInt(BankSupportAmount::getAmount).sum();
	}

	@Data
	@AllArgsConstructor
	public static class BankSupportAmount implements Serializable {
		private BankResponseItem bank;
		private Integer amount;
	}
}
