package com.railwayteam.railways.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class UtilsImpl {
	public static boolean isModLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}

	public static Path configDir() {
		return FabricLoader.getInstance().getConfigDir();
	}
}
