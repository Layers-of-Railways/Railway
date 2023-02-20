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
