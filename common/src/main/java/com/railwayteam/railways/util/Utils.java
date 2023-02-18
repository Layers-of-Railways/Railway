package com.railwayteam.railways.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class Utils {
	@ExpectPlatform
	public static boolean isModLoaded(String id) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Path configDir() {
		throw new AssertionError();
	}
}
