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
 * description : 기본 Error Response
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public enum DefaultErrorResponse implements ErrorResponse {
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request", "유효하지 않은 파라미티업니다.")
	,
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Not Authenticated", "로그인 이 필요한 서비스 입니다.")
	,
	FORBIDDEN(HttpStatus.FORBIDDEN, "Not Authorized", "권한이 없습니다.")
	,
	NOT_FOUND(HttpStatus.NOT_FOUND, "URL Not Found", "존재하지 않는 주소입니다.")
	,
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Not allowed method", "허용되지 않는 요청입니다.")
	,
	INVALID(HttpStatus.CONFLICT, "Invalid Business", "유효하지 않은 요청입니다.")
	,
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Not allowed media-type", "혀용되지 않는 요청입니다.")
	,
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", "일시적인 오류가 발생하였습니다. 잠시 후 다시 시도 해 주세요.")
	;

	final HttpStatus status;
	final String errorType;
	final String errorMessage;
}
