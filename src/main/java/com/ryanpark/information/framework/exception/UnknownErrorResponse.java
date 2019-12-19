/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-18
 * description : 정의 되지 않은 status를 위한 Error Response
 */
public class UnknownErrorResponse implements ErrorResponse {
	private HttpStatus status;

	UnknownErrorResponse(int status) {
		this.status = HttpStatus.valueOf(status);
	}

	@Override
	public HttpStatus getStatus() {
		return this.status;
	}

	@Override
	public String getErrorType() {
		return Optional.ofNullable(this.status)
				.map(HttpStatus::getReasonPhrase)
				.orElse(DefaultErrorResponse.INTERNAL_SERVER_ERROR.getErrorType())
				;
	}

	@Override
	public String getErrorMessage() {
		return DefaultErrorResponse.INTERNAL_SERVER_ERROR.getErrorMessage();
	}
}
