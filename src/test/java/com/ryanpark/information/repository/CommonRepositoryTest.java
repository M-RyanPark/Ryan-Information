/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description :
 */
@DataJpaTest
public class CommonRepositoryTest {
	@PersistenceContext
	protected EntityManager entityManager;
}
