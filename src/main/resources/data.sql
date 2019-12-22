/*
 * Copyright (c) 2019 Ryan Information Test
 */

INSERT INTO tb_account
	(
		user_no
		, user_id
		, password
		, active
		, status
	)
VALUES
	(
		1
		, 'admin'
		, '$2a$10$hFQj6wJFFRECCqS4cjkJ7OzbKeYfWVjlS9ln12eXyZz3M0aP.BYTa'
		, 'true'
		, 'ACTIVE'
	);

INSERT INTO ACCOUNT_AUTHORITIES
	(
		account_user_no
		, authority
	)
VALUES
	(
		1
		, 'ADMIN'
	);

INSERT INTO oauth_client_details
	(
		client_id,
		client_secret,
		resource_ids,
		scope,
		authorized_grant_types,
		web_server_redirect_uri,
		authorities,
		access_token_validity,
		refresh_token_validity,
		additional_information,
		autoapprove
		)
VALUES
	(
		'loan_client',
		'$2a$10$JZfpn0eI2UfXKvgdtUSENumr2Jr1xPmcErNzbtjpkf7blKzVGgmH.',
		'information-api',
		'read,write',
		'authorization_code,rest_api,refresh_token,client_credentials',
		'http://localhost:8080/callback',
		'USER',
		36000,
		2592000,
		null,
		'true'
	);
