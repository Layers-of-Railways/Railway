package com.railwayteam.railways.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class RegistrationListening {
	public static <T> void whenBothRegistered(Registry<T> registry, ResourceLocation id1, ResourceLocation id2, BiConsumer<T, T> consumer) {
		whenBothRegistered(registry, id1, registry, id2, consumer);
	}

	public static <T, U> void whenBothRegistered(Registry<T> registry1, ResourceLocation id1,
												 Registry<U> registry2, ResourceLocation id2,
												 BiConsumer<T, U> consumer) {
		Listener<T, U> listener = new Listener<>(registry1, id1, registry2, id2, consumer);
		addListener(listener);
	}

	@ExpectPlatform
	public static <T, U> void addListener(Listener<T, U> listener) {
		throw new AssertionError();
	}

	public static class Listener<T, U> {
		public final Registry<T> registry1;
		public final Registry<U> registry2;
		public final BiConsumer<T, U> consumer;
		public final ResourceLocation id1, id2;

		private boolean firstRegistered, secondRegistered;

		private Listener(Registry<T> registry1, ResourceLocation id1,
						 Registry<U> registry2, ResourceLocation id2,
						 BiConsumer<T, U> consumer) {
			this.registry1 = registry1;
			this.id1 = id1;
			this.registry2 = registry2;
			this.id2 = id2;
			this.consumer = consumer;
		}

		public void onRegister(ResourceLocation id) {
			if (id.equals(id1))
				firstRegistered = true;
			if (id.equals(id2))
				secondRegistered = true;
			if (firstRegistered && secondRegistered)
				bothRegistered();
		}

		public void bothRegistered() {
			T t = registry1.get(id1);
			U u = registry2.get(id2);
			consumer.accept(t, u);
		}
	}
}
