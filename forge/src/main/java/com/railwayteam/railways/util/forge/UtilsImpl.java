package com.railwayteam.railways.util.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class UtilsImpl {
	public static boolean isModLoaded(String id) {
		return ModList.get().isLoaded(id);
	}

	public static Path configDir() {
		return FMLPaths.CONFIGDIR.get();
	}
}
