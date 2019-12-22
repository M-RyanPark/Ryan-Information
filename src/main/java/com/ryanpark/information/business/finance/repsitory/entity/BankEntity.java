/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.repsitory.entity;

import com.ryanpark.information.common.repository.entity.BaseEntity;
import lombok.*;

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
@AllArgsConstructor
public class BankEntity extends BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(length = 4)
	private Integer code;

	@Column(length = 100, nullable = false, unique = true)
	private String name;

	public BankEntity(String name) {
		this.name = name;
	}

	public static BankEntity of(String name) {
		return new BankEntity(name);
	}
}
