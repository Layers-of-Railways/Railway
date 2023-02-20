package com.railwayteam.railways.util.fabric;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.railwayteam.railways.util.RegistrationListening.Listener;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegistrationListeningImpl {
	private static final Map<Registry<?>, Callback<?>> callbacks = new HashMap<>();

	public static <T> void addListener(Listener<T> listener) {
		//noinspection unchecked,rawtypes
		callbacks.computeIfAbsent(listener.registry(), Callback::new).addListener((Listener) listener);
	}

	private static class Callback<T> implements RegistryEntryAddedCallback<T> {
		private final Multimap<ResourceLocation, Listener<T>> listeners = HashMultimap.create();
		private final Registry<T> registry;
		private final Set<ResourceLocation> beforeListeningStart;

		public Callback(Registry<T> registry) {
			this.registry = registry;
			RegistryEntryAddedCallback.event(registry).register(this);
			beforeListeningStart = registry.keySet(); // returns unmodifiable set
		}

		protected void addListener(Listener<T> listener) {
			ResourceLocation id = listener.id();
			// if already registered, don't store it
			if (beforeListeningStart.contains(id))
				listener.onRegister(registry.get(id));
			else listeners.put(id, listener);
		}

		@Override
		public void onEntryAdded(int rawId, ResourceLocation id, T object) {
			listeners.get(id).forEach(listener -> listener.onRegister(object));
		}
	}
}
