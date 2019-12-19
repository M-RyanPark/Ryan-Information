/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-18
 * description : Error 메세지 처리를 위한 기본 interface
 */
public interface ErrorResponse {
	HttpStatus getStatus();
	String getErrorType();
	String getErrorMessage();

	static ErrorResponse of(int status) {
		Stream<ErrorResponse> stream = Stream.of(DefaultErrorResponse.values());

		return stream
				.filter(e -> e.getStatus().value() == status)
				.findFirst()
				.orElse(new UnknownErrorResponse(status))
				;
	}
}
