/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.api;

import com.ryanpark.information.framework.exception.BaseBusinessException;
import com.ryanpark.information.framework.exception.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : Exception Test를 위한 컨트롤러
 */
@RestController
public class ExceptionTestController {

	@GetMapping(value = "/test/error/{status}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void throwBusinessExceptionByErrorCode(@PathVariable("status") int status) {
		throw new BaseBusinessException(ErrorResponse.of(status));
	}
}
