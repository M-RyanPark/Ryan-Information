/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.controller;

import com.ryanpark.information.common.domain.api.SignInRequest;
import com.ryanpark.information.common.domain.api.SignInResponse;
import com.ryanpark.information.common.domain.api.SignUpRequest;
import com.ryanpark.information.common.domain.api.SignUpResponse;
import com.ryanpark.information.common.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description :
 */
@FrameworkEndpoint
@RequiredArgsConstructor
public class SignController {

	final SignService signService;

	@PostMapping("/sign/up")
	@ResponseBody
	public SignUpResponse signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
		return signService.signUp(signUpRequest);
	}

	@PostMapping("/sign/in")
	@ResponseBody
	public SignInResponse signIn(@RequestBody @Valid SignInRequest signInRequest) {
		return signService.signIn(signInRequest);
	}
}
