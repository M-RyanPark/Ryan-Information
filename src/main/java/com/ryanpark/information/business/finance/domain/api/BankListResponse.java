/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.domain.api;

import com.ryanpark.information.business.finance.domain.BankResponseItem;
import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융기관 리스트 response
 */
@Data
@AllArgsConstructor(staticName = "of")
public class BankListResponse implements Serializable {
	private String desc = "년도 별 주택 금융 공급 현황";
	private List<BankResponseItem> contents;

	public BankListResponse(@NonNull List<BankEntity> bankEntityList) {
		this.contents = bankEntityList.stream()
				.map(bankEntity -> BankResponseItem.of(bankEntity.getCode(), bankEntity.getName()))
				.collect(Collectors.toList())
		;
	}
}
