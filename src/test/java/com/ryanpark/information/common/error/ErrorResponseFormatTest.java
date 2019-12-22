/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.error;

import com.ryanpark.information.framework.config.ExceptionConfig;
import com.ryanpark.information.framework.exception.AuthenticationException;
import com.ryanpark.information.framework.exception.DefaultErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.RequestDispatcher;
import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : BasicErrorController 가 ExceptionConfig에 설정한 규격대로 반환 되는 지를 테스트
 */
@Slf4j
@WebMvcTest(
		controllers = BasicErrorController.class
)
@Import(ExceptionConfig.class)
@MockBean(classes = {UserDetailsService.class, DataSource.class})
public class ErrorResponseFormatTest {

	@Autowired
	MockMvc mockMvc;


	@Test
	public void error_response_format() throws Exception {
		int status = DefaultErrorResponse.UNAUTHORIZED.getStatus().value();
		String errorType = DefaultErrorResponse.UNAUTHORIZED.getErrorType();
		String errorMessage = DefaultErrorResponse.UNAUTHORIZED.getErrorMessage();

		mockMvc.perform(
				get("/error")
						.content(MediaType.APPLICATION_JSON_VALUE)
						.characterEncoding("UTF-8")
						.accept(MediaType.APPLICATION_JSON_UTF8_VALUE)	// deprecated 되었지만, print()를 사용하여 출력할 경우 console 상에서 한글이 깨지는 문제가 있어 설정함
						.requestAttr(RequestDispatcher.ERROR_STATUS_CODE, status)
						.requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/test")
						.requestAttr(RequestDispatcher.ERROR_MESSAGE, errorMessage)
						.requestAttr(RequestDispatcher.ERROR_EXCEPTION, new AuthenticationException())
		)
				.andDo(print())
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("errorType", Matchers.is(errorType)))
				.andExpect(jsonPath("message", Matchers.is(errorMessage)))
				;

	}
}


