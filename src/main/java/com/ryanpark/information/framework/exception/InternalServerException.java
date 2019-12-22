/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 서버 내부 에러를 처리하기 위한 Exception
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends BaseBusinessException {
	private static final DefaultErrorResponse defaultErrorResponse = DefaultErrorResponse.INTERNAL_SERVER_ERROR;

	public InternalServerException() {
		super(defaultErrorResponse);
	}

	private InternalServerException(String message) {
		super(CustomizableErrorResponse.of(defaultErrorResponse, message));
	}

	public static InternalServerException of (String message) {
		return new InternalServerException(message);
	}
}
