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
import com.railwayteam.railways.content.coupling.TrackCouplerDisplaySource;
import com.railwayteam.railways.content.distant_signals.SignalDisplaySource;
import com.railwayteam.railways.content.switches.SwitchDisplaySource;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class CRDisplaySources {
	public static RegistryEntry<TrackCouplerDisplaySource> TRACK_COUPLER_INFO = simple("track_coupler_info", TrackCouplerDisplaySource::new);
	public static RegistryEntry<SwitchDisplaySource> TRACK_SWITCH = simple("track_switch", SwitchDisplaySource::new);
	public static RegistryEntry<SignalDisplaySource> SIGNAL = simple("track_signal_source", SignalDisplaySource::new);
	
	private static <T extends DisplaySource> RegistryEntry<T> simple(String name, Supplier<T> supplier) {
		return Railways.registrate().displaySource(name, supplier).register();
	}

	public static void register() {}
}
