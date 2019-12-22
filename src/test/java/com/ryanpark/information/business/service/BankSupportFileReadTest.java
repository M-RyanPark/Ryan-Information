/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.service;

import com.ryanpark.information.business.finance.repsitory.entity.BankSupportEntity;
import com.ryanpark.information.business.finance.service.BankSupportFileReader;
import com.ryanpark.information.business.finance.service.impl.BankSupportFileReaderImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description :
 */
@SpringBootTest(classes = BankSupportFileReaderImpl.class)
@Slf4j
public class BankSupportFileReadTest {

	@Autowired BankSupportFileReader bankSupportFileReader;

	@Test
	public void csv_read_success_test() throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/test-data.csv");
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", is);

		List<BankSupportEntity> list =  bankSupportFileReader.readBankSupportData(mockMultipartFile);
		log.info("result = {}", list);

		int expectedResult = 20 * 9; // test-data.csv 의 유효 금융사 9개 20 row가 존재하므로 기대값은 180
		assertEquals(expectedResult, list.size());
	}
}
