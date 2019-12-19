package com.bjs.bjsapi.controllers.responses;

public class MaleFemaleResponse<T> {

	private T male;
	private T female;

	public MaleFemaleResponse(T male, T female) {
		this.male = male;
		this.female = female;
	}

	public MaleFemaleResponse() {
	}

	public T getMale() {
		return male;
	}

	public void setMale(T male) {
		this.male = male;
	}

	public T getFemale() {
		return female;
	}

	public void setFemale(T female) {
		this.female = female;
	}

}
