package com.bjs.bjsapi.controllers.responses;

import java.util.List;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopResponse<T> {

	private T first;
	private T second;
	private T third;

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
