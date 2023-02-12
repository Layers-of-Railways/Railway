package com.railwaysteam.railways.forge;

import com.railwayteam.railways.Railways;
import net.minecraftforge.fml.common.Mod;

@Mod(Railways.MODID)
public class RailwaysForge {
	public RailwaysForge() {
		Railways.init();
	}
}
