package com.railwayteam.railways.fabric;

import com.railwayteam.railways.events.CommonEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class CommonEventsImpl {
	public static void init() {
		ServerTickEvents.START_WORLD_TICK.register(CommonEvents::onWorldTickStart);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> CommonEvents.onPlayerJoin(handler.player));
	}
}
