package com.bjs.bjsapi.controllers.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaleFemaleResponse<T> {

	private T male;
	private T female;

}
