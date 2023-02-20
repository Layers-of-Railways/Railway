package com.railwayteam.railways.util.forge;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.railwayteam.railways.util.RegistrationListening.Listener;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class RegistrationListeningImpl {
	private static final Multimap<ResourceLocation, Listener<?, ?>> listeners = HashMultimap.create();

	public static <T, U> void addListener(Listener<T, U> listener) {
		listeners.put(listener.id1, listener);
		listeners.put(listener.id2, listener);
	}

	@SubscribeEvent
	public static void afterRegistration(InterModEnqueueEvent event) {
		event.enqueueWork(() -> listeners.forEach((id, listener) -> listener.bothRegistered()));
	}
}
