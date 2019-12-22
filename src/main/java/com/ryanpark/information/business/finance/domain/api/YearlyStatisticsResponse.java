/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.domain.api;

import com.ryanpark.information.business.finance.domain.YearlySupportData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 년간 전체 지관 지원 정보 Response
 */
@Data
public class YearlyStatisticsResponse implements Serializable {
	private String desc = "년도 별 주택 금융 공급 현황";
	private List<YearlySupportData> contents;

	public YearlyStatisticsResponse(List<YearlySupportData> supportList) {
		this.contents = supportList;
	}
}
