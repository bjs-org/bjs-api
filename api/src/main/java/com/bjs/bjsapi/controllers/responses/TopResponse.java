package com.bjs.bjsapi.controllers.responses;

import java.util.List;
import java.util.function.Function;

public class TopResponse<T> {

	private T first;
	private T second;
	private T third;

	public TopResponse() {
	}

	public TopResponse(T first, T second, T third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public T getSecond() {
		return second;
	}

	public void setSecond(T second) {
		this.second = second;
	}

	public T getThird() {
		return third;
	}

	public void setThird(T third) {
		this.third = third;
	}

	public static <T, S> TopResponse<S> of(List<T> list, Function<T, S> replace) {
		TopResponse<S> response = new TopResponse<>();

		int i = 0;
		for (T t : list) {
			if (i == 0) {
				response.setFirst(replace.apply(t));
			}

			if (i == 1) {
				response.setSecond(replace.apply(t));
			}

			if (i == 2) {
				response.setThird(replace.apply(t));
			}

			i++;
		}

		return response;
	}

}
