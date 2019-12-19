/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-18
 * description : Custom 메세지 처리를 위한 Error Response
 */
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomizableErrorResponse implements ErrorResponse {

	private HttpStatus status;
	private String errorType;
	private String errorMessage;

	public static CustomizableErrorResponse of(Integer httpStatus, String errorMessage) {
		ErrorResponse errorResponse = ErrorResponse.of(httpStatus);

		return new CustomizableErrorResponse(errorResponse.getStatus(), errorResponse.getErrorType(), errorMessage);
	}

	public static CustomizableErrorResponse of(DefaultErrorResponse errorResponse, String errorMessage) {
		return new CustomizableErrorResponse(errorResponse.getStatus(), errorResponse.getErrorType(), errorMessage);
	}
}
