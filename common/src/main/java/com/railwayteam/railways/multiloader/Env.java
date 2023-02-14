package com.railwayteam.railways.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.function.Supplier;

public class Env {
	public static final Env CLIENT = new Env();
	public static final Env SERVER = new Env();

	public static final Env CURRENT = getCurrent();

	public boolean isCurrent() {
		return this == CURRENT;
	}

	public void runIfCurrent(Supplier<Runnable> run) {
		if (isCurrent())
			run.get().run();
	}

	@ExpectPlatform
	public static Env getCurrent() {
		throw new AssertionError();
	}
}
