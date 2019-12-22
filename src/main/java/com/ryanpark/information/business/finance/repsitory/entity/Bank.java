/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.repsitory.entity;

import com.ryanpark.information.common.repository.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 기관 Entity
 */
@Entity
@Table(
		name = "tb_bank"
		, indexes = @Index(name = "bank_name_index", columnList = "name")
)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Bank extends BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(length = 4)
	private Integer code;

	@Column(length = 100, nullable = false, unique = true)
	private String name;

	public Bank(String name) {
		this.name = name;
	}

	public static Bank of(String name) {
		return new Bank(name);
	}
}