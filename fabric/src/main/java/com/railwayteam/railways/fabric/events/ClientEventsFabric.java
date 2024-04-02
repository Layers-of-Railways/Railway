package com.railwayteam.railways.fabric.events;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.events.ClientEvents;
import com.railwayteam.railways.registry.CRParticleTypes;
import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.KeyInputCallback;
import io.github.fabricators_of_create.porting_lib.event.client.ParticleManagerRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

public class ClientEventsFabric {
	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTickStart);
		ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::onClientTickEnd);
		KeyInputCallback.EVENT.register((key, scancode, action, mods) -> {
			ClientEvents.onKeyInput(key, action != 0);
		});
		ClientWorldEvents.LOAD.register((mc, level) -> ClientEvents.onClientWorldLoad(level));
		ParticleManagerRegistrationCallback.EVENT.register(CRParticleTypes::registerFactories);
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return Railways.asResource("client_events");
			}

			@Override
			public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
				ClientEvents.onTagsUpdated();
			}
		});
	}
}
