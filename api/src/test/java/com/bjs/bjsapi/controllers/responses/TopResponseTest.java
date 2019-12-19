package com.bjs.bjsapi.controllers.responses;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class TopResponseTest {

	@Test
	void ofNormal() {
		final TopResponse<String> of = TopResponse.of(Arrays.asList("1", "2", "3"), s -> s);

		assertThat(of.getFirst()).isEqualTo("1");
		assertThat(of.getSecond()).isEqualTo("2");
		assertThat(of.getThird()).isEqualTo("3");
	}

	@Test
	void ofReplaceWithNull() {
		final TopResponse<String> of = TopResponse.of(Arrays.asList("1", "2", "3"), s -> {
			if (s.equals("2")) {
				return null;
			} else {
				return s;
			}
		});

		assertThat(of.getFirst()).isEqualTo("1");
		assertThat(of.getSecond()).isEqualTo(null);
		assertThat(of.getThird()).isEqualTo("3");
	}

	@Test
	void ofReplaceWithSomethingElse() {
		final TopResponse<String> of = TopResponse.of(Arrays.asList("1", "2", "3"), s -> {
			if (s.equals("2")) {
				return "1";
			} else {
				return s;
			}
		});

		assertThat(of.getFirst()).isEqualTo("1");
		assertThat(of.getSecond()).isEqualTo("1");
		assertThat(of.getThird()).isEqualTo("3");
	}

}