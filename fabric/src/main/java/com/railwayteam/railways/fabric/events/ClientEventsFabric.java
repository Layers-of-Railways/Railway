package com.railwayteam.railways.fabric.events;

import com.railwayteam.railways.events.ClientEvents;
import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class ClientEventsFabric {
	public static void init() {
		ItemTooltipCallback.EVENT.register(ClientEvents::onTooltip);
		ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTickStart);
		ClientWorldEvents.LOAD.register((mc, level) -> ClientEvents.onClientWorldLoad(level));
	}
}
