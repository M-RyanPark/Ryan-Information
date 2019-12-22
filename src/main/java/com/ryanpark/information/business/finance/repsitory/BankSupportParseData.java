/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.repsitory;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-23
 * description : BankSupportEntity 생성을 위한 csv parse 결과
 */
@Data
@AllArgsConstructor(staticName = "of")
public class BankSupportParseData {
	private String name;
	private Integer year;
	private Integer month;
	private Integer amount;
}
