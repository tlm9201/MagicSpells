package com.nisovin.magicspells.volatilecode;

public interface VolatileCodeHelper {

	void error(String message);

	int scheduleDelayedTask(Runnable task, long delay);

}
