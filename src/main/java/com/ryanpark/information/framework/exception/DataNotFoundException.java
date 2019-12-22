/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 조회 한 Data가 없을 경우 처리할 Exception
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataNotFoundException extends BaseBusinessException {
	private static final DefaultErrorResponse defaultErrorResponse = DefaultErrorResponse.DATA_NOT_FOUND;

	public DataNotFoundException() {
		super(defaultErrorResponse);
	}

	private DataNotFoundException(String message) {
		super(CustomizableErrorResponse.of(defaultErrorResponse, message));
	}

	public static DataNotFoundException of (String message) {
		return new DataNotFoundException(message);
	}
}
