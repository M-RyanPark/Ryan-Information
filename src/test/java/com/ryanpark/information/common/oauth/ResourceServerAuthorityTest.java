/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import com.github.ahunigel.test.security.AttachClaims;
import com.github.ahunigel.test.security.Claim;
import com.github.ahunigel.test.security.oauth2.MockTokenServices;
import com.github.ahunigel.test.security.oauth2.WithMockOAuth2Client;
import com.github.ahunigel.test.security.oauth2.WithMockOAuth2User;
import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.common.service.TokenManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : ResourceServer 권한 테스트
 */
@WebMvcTest(
		controllers = ResourceServerConfigTestController.class
		, includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class, EnableResourceServer.class, EnableAuthorizationServer.class})
)
@MockBean(classes = {UserDetailsService.class, DataSource.class, AccountService.class, TokenManager.class})
@MockTokenServices
public class ResourceServerAuthorityTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	public void request_api_fail_with_no_user_authority() throws Exception{
		mockMvc.perform(get("/api/test")
				.content(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("UTF-8")
		)
				.andDo(print())
				.andExpect(status().isUnauthorized());
	}

	@WithMockOAuth2User(
			client = @WithMockOAuth2Client(clientId = "loan_client", scope = {"write"}, authorities = {"ADMIN"})
			, user = @WithMockUser(username = "test1", authorities = {"ADMIN"})
			, claims = @AttachClaims({
			@Claim(name = "user_no", value = "1", type = Long.class),
			@Claim(name = "role_id", value = "1"),
	})
	)
	@Test
	public void request_api_success_with_admin_role() throws Exception{
		mockMvc.perform(get("/api/test")
				.content(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("UTF-8")
		)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(is("ok")));
	}

	@Test
	@WithMockOAuth2User(
			client = @WithMockOAuth2Client(clientId = "loan_client", scope = {"write"}, authorities = {"USER"})
			, user = @WithMockUser(username = "test1", authorities = {"USER"})
			, claims = @AttachClaims({
			@Claim(name = "user_no", value = "1", type = Long.class),
			@Claim(name = "role_id", value = "1"),
	})
	)
	public void request_api_success_with_user_role() throws Exception{
		mockMvc.perform(get("/api/test")
				.content(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("UTF-8")
		)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(is("ok")));
	}

	@WithMockOAuth2User(
			client = @WithMockOAuth2Client(clientId = "loan_client", scope = {"read"}, authorities = {"ADMIN"})
			, user = @WithMockUser(username = "test1", authorities = {"ADMIN"})
	)
	@Test
	public void resource_api_fail_has_no_scope() throws Exception{
		mockMvc.perform(get("/api/test")
				.content(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("UTF-8")
		)
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@WithMockOAuth2User(
			client = @WithMockOAuth2Client(clientId = "loan_client", scope = {"write"}, authorities = {"USER2"})
			, user = @WithMockUser(username = "test1", authorities = {"USER2"})
	)
	@Test
	public void resource_api_fail_has_no_authorities() throws Exception{
		mockMvc.perform(get("/api/test")
				.content(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("UTF-8")
		)
				.andDo(print())
				.andExpect(status().isForbidden());
	}


	@WithMockOAuth2User(
			client = @WithMockOAuth2Client(clientId = "loan_client", scope = {"read", "write"}, authorities = {"ADMIN"})
			, user = @WithMockUser(username = "test1", authorities = {"ADMIN"})
	)
	@Test
	public void request_admin_success() throws Exception{
		mockMvc.perform(get("/admin/test")
				.content(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("UTF-8")
		)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(is("ok")));
	}

	@WithMockOAuth2User(
			client = @WithMockOAuth2Client(clientId = "loan_client", scope = {"read", "write"}, authorities = {"USER"})
			, user = @WithMockUser(username = "test1", authorities = {"USER"})
	)
	@Test
	public void request_admin_fail_no_authority() throws Exception{
		mockMvc.perform(get("/admin/test")
				.content(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("UTF-8")
		)
				.andDo(print())
				.andExpect(status().isForbidden());
	}
}
