package com.railwayteam.railways.fabric.events;

import com.railwayteam.railways.events.ClientEvents;
import com.railwayteam.railways.registry.CRParticleTypes;
import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.KeyInputCallback;
import io.github.fabricators_of_create.porting_lib.event.client.ParticleManagerRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientEventsFabric {
	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTickStart);
		ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::onClientTickEnd);
		KeyInputCallback.EVENT.register((key, scancode, action, mods) -> {
			ClientEvents.onKeyInput(key, action != 0);
		});
		ClientWorldEvents.LOAD.register((mc, level) -> ClientEvents.onClientWorldLoad(level));
		ParticleManagerRegistrationCallback.EVENT.register(CRParticleTypes::registerFactories);
	}
}
