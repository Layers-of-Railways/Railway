package com.railwayteam.railways.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class UtilsImpl {
	public static boolean isModLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}
}
