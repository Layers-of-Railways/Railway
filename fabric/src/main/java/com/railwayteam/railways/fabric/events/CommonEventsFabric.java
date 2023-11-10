package com.railwayteam.railways.fabric.events;

import com.railwayteam.railways.events.CommonEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class CommonEventsFabric {
	public static void init() {
		ServerTickEvents.START_WORLD_TICK.register(CommonEvents::onWorldTickStart);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> CommonEvents.onPlayerJoin(handler.player));
		ServerWorldEvents.LOAD.register((server, level) -> CommonEvents.backupDisplayRegister());
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(((server, resourceManager, success) -> {
			CommonEvents.onTagsUpdated();
		}));
	}
}
