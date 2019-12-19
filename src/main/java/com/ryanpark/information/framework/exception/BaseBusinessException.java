/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import lombok.Getter;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-18
 * description : Exception 처리를 위한 기본 클래스
 */
public class BaseBusinessException extends RuntimeException {
	@Getter
	protected ErrorResponse errorResponse;

	protected BaseBusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseBusinessException(ErrorResponse errorResponse) {
		super(errorResponse.getErrorMessage());
		this.errorResponse = errorResponse;
	}
}
