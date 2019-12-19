/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-18
 * description : 인증 예외 처리를 위한 Exception
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends BaseBusinessException {
	private static final DefaultErrorResponse defaultErrorResponse = DefaultErrorResponse.UNAUTHORIZED;

	public AuthenticationException() {
		super(defaultErrorResponse);
	}

	private AuthenticationException(String message) {
		super(CustomizableErrorResponse.of(defaultErrorResponse, message));
	}

	public static AuthenticationException of (String message) {
		return new AuthenticationException(message);
	}
}
