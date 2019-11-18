package com.bjs.bjsapi.helper;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;

class JsonHelper {

	public static BaseMatcher<Object> printMatcher(final Logger log) {
		return new BaseMatcher<Object>() {
			@Override
			public boolean matches(Object item) {
				log.info(item.toString());
				return true;
			}

			@Override
			public void describeTo(Description description) {

			}
		};
	}

}
