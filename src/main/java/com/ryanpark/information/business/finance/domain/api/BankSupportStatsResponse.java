/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.domain.api;

import com.ryanpark.information.business.finance.domain.BankResponseItem;
import com.ryanpark.information.business.finance.domain.BankSupportData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-23
 * description : 금융 지원 통계 Response
 */
@Data
public class BankSupportStatsResponse implements Serializable {
	private BankResponseItem bank;
	private YearSupportAmount maxSupport;
	private YearSupportAmount minSupport;

	public BankSupportStatsResponse(BankSupportData bankSupportData) {
		this.bank = bankSupportData.getBank();
		bankSupportData.getMaxSupportOfYear()
				.ifPresent(max -> {
					this.maxSupport = YearSupportAmount.of(max.getYear(), max.getAverage());
				});
		bankSupportData.getMinSupportOfYear()
				.ifPresent(min -> {
					this.minSupport = YearSupportAmount.of(min.getYear(), min.getAverage());
				});
	}

	@Data
	@AllArgsConstructor(staticName = "of")
	public static class YearSupportAmount implements Serializable {
		private Integer year;
		private Integer amount;
	}
}
