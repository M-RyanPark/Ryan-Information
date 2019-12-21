/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : 잘못된 Client 요청을 처리하기 위한 Exception
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends BaseBusinessException {
	private static final DefaultErrorResponse defaultErrorResponse = DefaultErrorResponse.BAD_REQUEST;

	public BadRequestException() {
		super(defaultErrorResponse);
	}

	private BadRequestException(String message) {
		super(CustomizableErrorResponse.of(defaultErrorResponse, message));
	}

	public static BadRequestException of (String message) {
		return new BadRequestException(message);
	}
}
