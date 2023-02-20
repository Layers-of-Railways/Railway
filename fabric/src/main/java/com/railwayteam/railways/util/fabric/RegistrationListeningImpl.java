package com.railwayteam.railways.util.fabric;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.railwayteam.railways.util.RegistrationListening.Listener;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegistrationListeningImpl {
	private static final Map<Registry<?>, Callback<?>> callbacks = new HashMap<>();

	public static <T, U> void addListener(Listener<T, U> listener) {
		callbacks.computeIfAbsent(listener.registry1, Callback::new).addListener(listener);
		callbacks.computeIfAbsent(listener.registry2, Callback::new).addListener(listener);
	}

	private static class Callback<T> implements RegistryEntryAddedCallback<T> {
		protected final Multimap<ResourceLocation, Listener<?, ?>> listeners = HashMultimap.create();
		protected final Set<ResourceLocation> beforeListeningStart;

		public Callback(Registry<T> registry) {
			RegistryEntryAddedCallback.event(registry).register(this);
			beforeListeningStart = registry.keySet(); // returns unmodifiable set
		}

		protected void addListener(Listener<?, ?> listener) {
			listeners.put(listener.id1, listener);
			listeners.put(listener.id2, listener);
			if (beforeListeningStart.contains(listener.id1))
				listener.onRegister(listener.id1);
			if (beforeListeningStart.contains(listener.id2))
				listener.onRegister(listener.id2);
		}

		@Override
		public void onEntryAdded(int rawId, ResourceLocation id, T object) {
			listeners.get(id).forEach(listener -> listener.onRegister(id));
		}
	}
}
