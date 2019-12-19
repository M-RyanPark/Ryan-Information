/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.error;

import com.ryanpark.information.framework.exception.AuthenticationException;
import com.ryanpark.information.framework.exception.DefaultErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.RequestDispatcher;

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
@SpringBootTest
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@ActiveProfiles("test-non-secure")
public class ErrorResponseFormatTest {

	MockMvc mockMvc;

	@Autowired
	WebApplicationContext context;

	// account WebSecurityConfig 를 disable 하여 passwordEncoder 가 없어 mock을 생성해준다
	@MockBean
	PasswordEncoder passwordEncoder;

	@BeforeEach
	public void prepare() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.alwaysDo(print())
				.build();
	}
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

