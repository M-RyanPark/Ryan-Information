/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.domain.api;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : 로그인 요청
 */
@Data
public class SignInRequest implements Serializable {
	@NotNull
	String userId;
	@NotNull
	String password;
}
