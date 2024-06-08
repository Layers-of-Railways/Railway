/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
