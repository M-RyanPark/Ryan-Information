/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-18
 * description : 권한 예외 처리를 위한 Exception
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthorityException extends BaseBusinessException {
	private static final DefaultErrorResponse defaultErrorResponse = DefaultErrorResponse.FORBIDDEN;

	public AuthorityException() {
		super(defaultErrorResponse);
	}

	private AuthorityException(String message) {
		super(CustomizableErrorResponse.of(defaultErrorResponse, message));
	}

	public static AuthorityException of (String message) {
		return new AuthorityException(message);
	}
}
