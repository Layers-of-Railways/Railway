/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.distant_signals.SemaphoreDisplayTarget;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class CRDisplayTargets {
	public static RegistryEntry<SemaphoreDisplayTarget> SEMAPHORE = simple("semaphore", SemaphoreDisplayTarget::new);

	private static <T extends DisplayTarget> RegistryEntry<T> simple(String name, Supplier<T> supplier) {
		return Railways.registrate().displayTarget(name, supplier).register();
	}

	public static void register() {}
}
