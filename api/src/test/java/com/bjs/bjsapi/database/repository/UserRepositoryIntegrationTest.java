package com.bjs.bjsapi.database.repository;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.bjs.bjsapi.helper.SecurityHelper;

public class UserRepositoryIntegrationTest extends RepositoryIntegrationTest {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		setupUserScenario();
		SecurityHelper.reset();
	}

	@Test
	public void test_true() {
		Assertions.assertThat(true).isTrue();
	}

	private void setupUserScenario() {

	}

}
