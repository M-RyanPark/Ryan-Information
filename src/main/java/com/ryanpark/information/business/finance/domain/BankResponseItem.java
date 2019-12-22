/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.domain;

import com.ryanpark.information.business.finance.repsitory.entity.BankEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-23
 * description : 은행 정보 response
 */
@Data
@AllArgsConstructor(staticName = "of")
public class BankResponseItem {
	private Integer code;
	private String name;

	public static BankResponseItem of(BankEntity bankEntity) {
		return new BankResponseItem(bankEntity.getCode(), bankEntity.getName());
	}
}
