/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.domain.api;

import com.ryanpark.information.business.finance.domain.BankResponseItem;
import com.ryanpark.information.business.finance.domain.BankSupportData;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-23
 * description : 년간 최고액 지원 금융 기관 response
 */
@Data
public class TopSupportBankResponse implements Serializable {
	private BankResponseItem bank;
	private Integer year;
	private Integer totalAmount;

	public TopSupportBankResponse(BankSupportData bankSupportData) {
		this.year = bankSupportData.getYearSupportAmountList().get(0).getYear();
		this.bank = bankSupportData.getBank();
		this.totalAmount = bankSupportData.getTotalSupportAmount();
	}
}
