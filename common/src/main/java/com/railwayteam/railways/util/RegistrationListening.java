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

package com.railwayteam.railways.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RegistrationListening {
	public static <T> void whenRegistered(Registry<T> registry, ResourceLocation id, Consumer<T> consumer) {
		addListener(new Listener<>(registry, id, consumer));
	}

	public static <T> void whenBothRegistered(Registry<T> registry, ResourceLocation id1, ResourceLocation id2, BiConsumer<T, T> consumer) {
		whenBothRegistered(registry, id1, registry, id2, consumer);
	}

	public static <T, U> void whenBothRegistered(Registry<T> registry1, ResourceLocation id1,
												 Registry<U> registry2, ResourceLocation id2,
												 BiConsumer<T, U> consumer) {
		DualListener<T, U> dual = new DualListener<>(registry1, id1, registry2, id2, consumer);
		addListener(dual.listener1);
		addListener(dual.listener2);
	}

	@ExpectPlatform
	public static <T> void addListener(Listener<T> listener) {
		throw new AssertionError();
	}

	public record Listener<T>(Registry<T> registry, ResourceLocation id, Consumer<T> consumer) {
		public void onRegister(T obj) {
			consumer.accept(obj);
		}
	}

	public static class DualListener<T, U> {
		public final Listener<T> listener1;
		public final Listener<U> listener2;
		private final BiConsumer<T, U> consumer;
		private T first;
		private U second;

		public DualListener(Registry<T> registry1, ResourceLocation id1,
							Registry<U> registry2, ResourceLocation id2,
							BiConsumer<T, U> consumer) {
			this.consumer = consumer;
			listener1 = new Listener<>(registry1, id1, this::firstRegistered);
			listener2 = new Listener<>(registry2, id2, this::secondRegistered);
		}

		private void firstRegistered(T first) {
			this.first = first;
			if (second != null)
				bothRegistered();
		}

		private void secondRegistered(U second) {
			this.second = second;
			if (first != null)
				bothRegistered();
		}

		private void bothRegistered() {
			consumer.accept(first, second);
		}
	}
}
