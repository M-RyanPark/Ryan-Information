/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.ryanpark.information.business.finance.repsitory.BankSupportParseData;
import com.ryanpark.information.business.finance.service.BankSupportFileReader;
import com.ryanpark.information.framework.exception.BusinessValidationException;
import com.ryanpark.information.framework.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : Csv 파일로부터 금융 지원 데이터 처리할 서비스
 */
@Service
@Slf4j
public class BankSupportFileReaderImpl implements BankSupportFileReader {

	@Override
	public List<BankSupportParseData> readBankSupportData(MultipartFile multipartFile) {
		List<String[]> parsedRawData;
		try {
			InputStreamReader input = new InputStreamReader(multipartFile.getInputStream());
			parsedRawData = parseCsv(input);
		} catch (IOException | CsvException e) {
			log.error("CSV 파일 처리 중 에러 : {}", e.getMessage());
			throw new InternalServerException();
		}

		if (CollectionUtils.isEmpty(parsedRawData) || parsedRawData.size() < 2) {
			throw BusinessValidationException.of("CSV 데이터 형식이 잘못 되었습니다.");
		}

		List<String> headerList = Stream.of(parsedRawData.get(0))
				.filter(Strings::isNotEmpty)
				.map(this::parseBankName)
				.collect(Collectors.toList())
				;

		List<List<Integer>> bodyList = parsedBodyData(parsedRawData);

		final List<BankSupportParseData> resultList = new ArrayList<>();

		parseInvalidSizeBodyList(bodyList, headerList.size())
				.forEach(list -> {
					int year = list.get(0);
					int month = list.get(1);

					IntStream.range(2, list.size())
							.forEach(i -> {
								String bankName = headerList.get(i);
								resultList.add(BankSupportParseData.of(bankName, year, month, list.get(i))) ;
							});
				});

		return resultList;
	}

	private List<List<Integer>> parsedBodyData(List<String[]> parsedRawData) {
		return parsedRawData.stream()
				.skip(1)
				.map(array -> {
					try {
						return Stream.of(array)
								.filter(Strings::isNotEmpty)
								.map(String::trim)
								.map(s -> s.replaceAll("[^0-9]",""))
								.map(Integer::parseInt)
								.collect(Collectors.toList())
								;
					} catch (Exception e) {
						log.warn("csv item is ignored : {}", e.getMessage());
						return null;
					}
				})
				.collect(Collectors.toList())
				;
	}
	private List<List<Integer>> parseInvalidSizeBodyList(List<List<Integer>> originalBodyList, int headerColumnCount) {
		return originalBodyList.stream()
				.filter(list -> {
					if (list.size() == headerColumnCount) {
						return true;
					} else {
						log.warn("bodyList is ignored = {}", list);
						return false;
					}
				})
				.collect(Collectors.toList());
	}
	private String parseBankName(String headerItem) {
		return headerItem.trim().replace("(억원)", "");
	}

	public List<String[]> parseCsv(Reader reader) throws IOException, CsvException {
		CSVReader csvReader = new CSVReader(reader);
		return csvReader.readAll();
	}
}
