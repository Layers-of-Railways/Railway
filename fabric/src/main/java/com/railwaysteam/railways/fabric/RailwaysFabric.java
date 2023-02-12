package com.railwaysteam.railways.fabric;

import com.railwayteam.railways.Railways;
import net.fabricmc.api.ModInitializer;

public class RailwaysFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Railways.init();
	}
}
