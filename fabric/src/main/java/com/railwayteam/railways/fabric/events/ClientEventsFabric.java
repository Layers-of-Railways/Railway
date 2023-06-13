package com.railwayteam.railways.fabric.events;

import com.railwayteam.railways.events.ClientEvents;
import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientEventsFabric {
	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTickStart);
		ClientWorldEvents.LOAD.register((mc, level) -> ClientEvents.onClientWorldLoad(level));
	}
}
