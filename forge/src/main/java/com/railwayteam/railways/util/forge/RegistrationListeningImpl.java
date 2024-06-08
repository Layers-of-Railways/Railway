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

package com.railwayteam.railways.util.forge;

import com.railwayteam.railways.util.RegistrationListening.Listener;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(bus = Bus.MOD)
public class RegistrationListeningImpl {
	private static final Set<Listener<?>> listeners = new HashSet<>();

	public static <T> void addListener(Listener<T> listener) {
		listeners.add(listener);
	}

	@SubscribeEvent
	public static void afterRegistration(InterModEnqueueEvent event) {
		event.enqueueWork(() -> listeners.forEach(RegistrationListeningImpl::handle));
	}

	private static <T> void handle(Listener<T> listener) {
		ResourceLocation id = listener.id();
		T obj = listener.registry().get(id);
		if (obj != null)
			listener.onRegister(obj);
	}
}
