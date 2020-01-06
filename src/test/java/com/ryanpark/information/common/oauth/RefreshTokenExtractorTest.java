/*
 * Copyright (c) 2020 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import com.ryanpark.information.framework.oauth.RefreshBearerTokenExtractor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2020-01-04
 * description :
 */
@Slf4j
public class RefreshTokenExtractorTest {

	private final RefreshBearerTokenExtractor refreshBearerTokenExtractor = new RefreshBearerTokenExtractor();
	private final String AUTHORIZATION_HEADER_NAME = "Authorization";
	
	@Test
	public void success_null_authentication_return_when_authorization_headers_empty() {
		MockHttpServletRequest spyRequest = spy(new MockHttpServletRequest());
		given(spyRequest.getHeaders(AUTHORIZATION_HEADER_NAME))
				.willReturn(Collections.emptyEnumeration());

		Authentication authentication = refreshBearerTokenExtractor.extract(spyRequest);
		log.info("authentication = {}", authentication);

		assertNull(authentication);
		verify(spyRequest, never()).setAttribute(anyString(), any());
	}

	@Test
	public void success_extract_refresh_type_with_one_bearer_refresh_type_header() {
		final String tokenValue = randomString();
		MockHttpServletRequest spyRequest = spy(new MockHttpServletRequest());
		given(spyRequest.getHeaders(AUTHORIZATION_HEADER_NAME))
				.willReturn(Collections.enumeration(
						Arrays.asList(RefreshBearerTokenExtractor.BEARER_REFRESH_TYPE + " " + tokenValue))
				);

		Authentication authentication = refreshBearerTokenExtractor.extract(spyRequest);
		log.info("authentication = {}", authentication);

		assertEquals(tokenValue, authentication.getPrincipal());
		verify(spyRequest, times(2)).setAttribute(anyString(), any());
		assertTrue((boolean)spyRequest.getAttribute(RefreshBearerTokenExtractor.BEARER_REFRESH_REQUIRED));
	}

	@Test
	public void success_extract_refresh_type_with_one_more_bearer_refresh_type_and_access_token_type_header() {
		final String tokenValue = randomString();
		final String tokenValue2 = randomString();
		final String tokenValue3 = randomString();

		MockHttpServletRequest spyRequest = spy(new MockHttpServletRequest());
		given(spyRequest.getHeaders(AUTHORIZATION_HEADER_NAME))
				.willReturn(Collections.enumeration(Arrays.asList(
						OAuth2AccessToken.BEARER_TYPE + " " + tokenValue2
						, RefreshBearerTokenExtractor.BEARER_REFRESH_TYPE + " " + tokenValue
						, OAuth2AccessToken.BEARER_TYPE + " " + tokenValue3
						))
				);

		Authentication authentication = refreshBearerTokenExtractor.extract(spyRequest);
		log.info("authentication = {}", authentication);

		assertEquals(tokenValue, authentication.getPrincipal());
		verify(spyRequest, times(2)).setAttribute(anyString(), any());
		assertTrue((boolean)spyRequest.getAttribute(RefreshBearerTokenExtractor.BEARER_REFRESH_REQUIRED));
	}

	@Test
	public void success_extract_refresh_type_with_token_include_comma_value() {
		final String tokenValue = randomString();
		final String tokenValue2 = randomString();
		MockHttpServletRequest spyRequest = spy(new MockHttpServletRequest());
		given(spyRequest.getHeaders(AUTHORIZATION_HEADER_NAME))
				.willReturn(Collections.enumeration(
						Arrays.asList(RefreshBearerTokenExtractor.BEARER_REFRESH_TYPE + " " + tokenValue + " , " + tokenValue2))
				);

		Authentication authentication = refreshBearerTokenExtractor.extract(spyRequest);
		log.info("authentication = {}", authentication);

		assertEquals(tokenValue, authentication.getPrincipal());
		verify(spyRequest, times(2)).setAttribute(anyString(), any());
		assertTrue((boolean)spyRequest.getAttribute(RefreshBearerTokenExtractor.BEARER_REFRESH_REQUIRED));
	}

	@Test
	public void success_extract_bearer_type_without_BEARER_REFRESH_REQUIRED_set() {
		final String tokenValue = randomString();
		MockHttpServletRequest spyRequest = spy(new MockHttpServletRequest());
		given(spyRequest.getHeaders(AUTHORIZATION_HEADER_NAME))
				.willReturn(Collections.enumeration(
						Arrays.asList(OAuth2AccessToken.BEARER_TYPE + " " + tokenValue))
				);

		Authentication authentication = refreshBearerTokenExtractor.extract(spyRequest);
		log.info("authentication = {}", authentication);

		assertEquals(tokenValue, authentication.getPrincipal());
		verify(spyRequest, times(1)).setAttribute(anyString(), any());
		assertNull(spyRequest.getAttribute(RefreshBearerTokenExtractor.BEARER_REFRESH_REQUIRED));
	}

	@Test
	public void same_extract_behavior_BearerTokenExtractor_and_RefreshBearerTokenExtractor() {
		final BearerTokenExtractor bearerTokenExtractor = new BearerTokenExtractor();
		final String tokenValue = randomString();
		MockHttpServletRequest spyRequest = spy(new MockHttpServletRequest());
		given(spyRequest.getHeaders(AUTHORIZATION_HEADER_NAME))
				.willReturn(Collections.enumeration(
						Arrays.asList(OAuth2AccessToken.BEARER_TYPE + " " + tokenValue))
				);
		MockHttpServletRequest spyRequest2 = spy(new MockHttpServletRequest());
		given(spyRequest2.getHeaders(AUTHORIZATION_HEADER_NAME))
				.willReturn(Collections.enumeration(
						Arrays.asList(OAuth2AccessToken.BEARER_TYPE + " " + tokenValue))
				);

		Authentication authentication1 = refreshBearerTokenExtractor.extract(spyRequest);
		log.info("authentication1 = {}", authentication1);
		Authentication authentication2 = bearerTokenExtractor.extract(spyRequest2);
		log.info("authentication2 = {}", authentication2);

		assertEquals(authentication1.getPrincipal(), authentication2.getPrincipal());
		verify(spyRequest, times(1)).setAttribute(anyString(), any());
		verify(spyRequest2, times(1)).setAttribute(anyString(), any());
	}


	private String randomString() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
