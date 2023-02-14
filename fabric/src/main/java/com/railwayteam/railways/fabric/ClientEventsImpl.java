package com.railwayteam.railways.fabric;

import com.railwayteam.railways.events.ClientEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class ClientEventsImpl {
	public static void init() {
		ItemTooltipCallback.EVENT.register((ClientEvents::onTooltip));
		ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTickStart);
	}
}
