package com.bjs.bjsapi.database.model.enums;

/**
 * Discipline types according to https://www.bundesjugendspiele.de/wai1/showcontent.asp?ThemaID=452
 */
public enum DisciplineType {

	RUN_50(true,50),
	RUN_75(true,75),
	RUN_100(true,100),
	RUN_800(true,800),
	RUN_2000(true,2000),
	RUN_3000(true,3000),

	HIGH_JUMP(false),
	LONG_JUMP(false),
	SHOT_PUT(false),
	SLING_BALL(false),
	BALL_THROWING_80(false),
	BALL_THROWING_200(false);

	private final boolean isRun;
	private int distance;

	public int getDistance() {
		return distance;
	}

	DisciplineType(boolean isRun, int distance) {
		this.isRun = isRun;
		this.distance = distance;
	}

	DisciplineType(boolean isRun) {
		this.isRun = isRun;
	}

	public boolean isRUN(){
		return isRun;
	}

}
