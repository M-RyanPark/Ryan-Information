/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanpark.information.framework.config.AuthorizationServerConfig;
import com.ryanpark.information.framework.config.WebSecurityConfig;
import com.ryanpark.information.framework.exception.DefaultErrorResponse;
import com.ryanpark.information.framework.exception.ErrorResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.stream.Stream;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : BaseBusinessException 을 이용하여 throw 가 발생 했을 때, error response 규격 대로 정상적으로 반환 되는지 테스트
 * mockMvc 는 throw가 발생해도 ErrorController로 redirect 되지 않기 때문에 TestRestTemplate을 이용한다
 *
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@ActiveProfiles("test-non-secure")
public class BaseBusinessExceptionTest {

	private static final String ERROR_TEST_PATH_FORMAT = "/test/error/%s";

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	ObjectMapper objectMapper;

	// account WebSecurityConfig 를 disable 하여 passwordEncoder 가 없어 mock을 생성해준다
	@MockBean
	PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setUp() {
		testRestTemplate.getRestTemplate()
				.setInterceptors(
						Collections.singletonList(
								(request, body, execution) -> {
									request.getHeaders()
											.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
									request.getHeaders()
											.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
									return execution.execute(request, body);
								})
				);
	}

	@Test
	public void check_error_response_of_all_DefaultErrorResponse() {
		Stream.of(DefaultErrorResponse.values())
				.forEach(this::check_error_response);
	}

	private void check_error_response(ErrorResponse errorResponse){
		String requestPath = String.format(ERROR_TEST_PATH_FORMAT, errorResponse.getStatus().value());

		String expected;

		try {
			expected = objectMapper.writeValueAsString(ErrorType.of(errorResponse));
		} catch (JsonProcessingException e) {
			log.error("Json String 생성 실패 : {}", e.getMessage());
			expected = "";
		}

		ResponseEntity<String> response = testRestTemplate.getForEntity(requestPath, String.class);
		log.info("body={}", response.getBody());

		Assertions.assertEquals(expected, response.getBody(), errorResponse.toString() + " fail");
	}

	@Data
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	static class ErrorType {
		private String errorType;
		private String message;

		static ErrorType of(@NonNull ErrorResponse errorResponse) {
			return new ErrorType(errorResponse.getErrorType(), errorResponse.getErrorMessage());
		}
	}
}
