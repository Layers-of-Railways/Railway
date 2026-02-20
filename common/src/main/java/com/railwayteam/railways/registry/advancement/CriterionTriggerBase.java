/*
 * Steam 'n' Rails
 * Copyright (c) 2023-2025 The Railways Team
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

package com.railwayteam.railways.registry.advancement;

import com.google.common.collect.Maps;
import com.railwayteam.railways.Railways;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CriterionTriggerBase<T extends CriterionTriggerBase.Instance> implements CriterionTrigger<T> {

	public CriterionTriggerBase(String id) {
		this.id = Railways.asResource(id);
	}

	private final ResourceLocation id;
	protected final Map<PlayerAdvancements, Set<Listener<T>>> listeners = Maps.newHashMap();

	@Override
	public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
		Set<Listener<T>> playerListeners = this.listeners.computeIfAbsent(playerAdvancementsIn, k -> new HashSet<>());

		playerListeners.add(listener);
	}

	@Override
	public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
		Set<Listener<T>> playerListeners = this.listeners.get(playerAdvancementsIn);
		if (playerListeners != null) {
			playerListeners.remove(listener);
			if (playerListeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	protected void trigger(ServerPlayer player, @Nullable List<Supplier<Object>> suppliers) {
		PlayerAdvancements playerAdvancements = player.getAdvancements();
		Set<Listener<T>> playerListeners = this.listeners.get(playerAdvancements);
		if (playerListeners != null) {
			List<Listener<T>> list = new LinkedList<>();

			for (Listener<T> listener : playerListeners) {
				if (listener.getTriggerInstance()
					.test(suppliers)) {
					list.add(listener);
				}
			}

			list.forEach(listener -> listener.run(playerAdvancements));

		}
	}

	public abstract static class Instance extends AbstractCriterionTriggerInstance {

		public Instance(ResourceLocation idIn, ContextAwarePredicate predicate) {
			super(idIn, predicate);
		}

		protected abstract boolean test(@Nullable List<Supplier<Object>> suppliers);
	}

}
