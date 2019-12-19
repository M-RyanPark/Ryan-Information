/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-18
 * description : 비지니스 처리 로직 검증을 위한 Exception
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BusinessValidationException extends BaseBusinessException {
	private static final DefaultErrorResponse defaultErrorResponse = DefaultErrorResponse.INVALID;

	public BusinessValidationException() {
		super(defaultErrorResponse);
	}

	private BusinessValidationException(String message) {
		super(CustomizableErrorResponse.of(defaultErrorResponse, message));
	}

	public static BusinessValidationException of (String message) {
		return new BusinessValidationException(message);
	}
}