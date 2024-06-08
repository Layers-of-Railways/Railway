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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public class CRSounds {
    public static final RegistryEntry<SoundEvent> CONDUCTOR_WHISTLE = Railways.registrate()
        .simple(
            "conductor_whistle",
            Registries.SOUND_EVENT,
            () -> SoundEvent.createVariableRangeEvent(Railways.asResource("conductor_whistle"))
        );

    public static final RegistryEntry<SoundEvent> HANDCAR_COGS = Railways.registrate()
        .simple(
            "handcar_cogs",
            Registries.SOUND_EVENT,
            () -> SoundEvent.createVariableRangeEvent(Railways.asResource("handcar_cogs"))
        );

    public static void register() {
    }
}
