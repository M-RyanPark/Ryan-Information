/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.config;

import com.ryanpark.information.framework.exception.BaseBusinessException;
import com.ryanpark.information.framework.exception.CustomizableErrorResponse;
import com.ryanpark.information.framework.exception.ErrorResponse;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-18
 * description : Exception Config
 */
@Configuration
public class ExceptionConfig {

	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {
			@Override
			public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
				Map<String, Object> errorAttributes = new LinkedHashMap<>();

				ErrorResponse errorResponse = Optional.ofNullable(getError(webRequest))
						.filter(throwable -> BaseBusinessException.class.isAssignableFrom(throwable.getClass()))
						.map(throwable -> ((BaseBusinessException)throwable).getErrorResponse())
						.orElse(errorResponse(webRequest))
						;

				errorAttributes.put("errorType", errorResponse.getErrorType());
				errorAttributes.put("message", errorResponse.getErrorMessage());

				return errorAttributes;
			}

			private ErrorResponse errorResponse(WebRequest webRequest) {
				return CustomizableErrorResponse.of(errorStatus(webRequest), extractErrorMessage(webRequest));
			}

			private Integer errorStatus(RequestAttributes requestAttributes) {
				return getAttribute(requestAttributes, "javax.servlet.error.status_code");
			}

			private String extractErrorMessage(WebRequest webRequest) {
				return Optional.ofNullable(getError(webRequest))
						.map(this::getErrorMessage)
						.orElseGet(() -> ErrorResponse.of(errorStatus(webRequest)).getErrorMessage());
			}

			private String getErrorMessage(Throwable throwable) {
				BindingResult result = extractBindingResult(throwable);

				if (Objects.isNull(result)) {
					return throwable.getMessage();
				}

				if (result.hasErrors()) {
					return "Validation failed for object='" + result.getObjectName()
							+ "'. Error count: " + result.getErrorCount();
				} else {
					return "No errors";
				}
			}

			private BindingResult extractBindingResult(Throwable error) {
				if (error instanceof BindingResult) {
					return (BindingResult) error;
				}
				if (error instanceof MethodArgumentNotValidException) {
					return ((MethodArgumentNotValidException) error).getBindingResult();
				}
				return null;
			}

			private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
				return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
			}
		};
	}
}
