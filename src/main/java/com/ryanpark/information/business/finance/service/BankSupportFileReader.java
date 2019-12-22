/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.service;

import com.ryanpark.information.business.finance.repsitory.BankSupportParseData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : Csv 파일로부터 금융 지원 데이터 처리할 서비스
 */
public interface BankSupportFileReader {
	List<BankSupportParseData> readBankSupportData(MultipartFile multipartFile);
}
