package com.railwayteam.railways.util.forge;

import net.minecraftforge.fml.ModList;

public class UtilsImpl {
	public static boolean isModLoaded(String id) {
		return ModList.get().isLoaded(id);
	}
}
