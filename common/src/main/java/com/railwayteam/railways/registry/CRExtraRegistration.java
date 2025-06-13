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

import com.google.common.collect.ImmutableSet;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.distant_signals.SignalDisplaySource;
import com.railwayteam.railways.mixin.AccessorBlockEntityType;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class CRExtraRegistration {
    public static boolean registeredSignalSource = false;
    public static boolean registeredVentAsCopycat = false;

    public static void addVentAsCopycat(BlockEntityType<?> object) {
        if (registeredVentAsCopycat) return;
        Block ventBlock;
        try {
            ventBlock = CRBlocks.CONDUCTOR_VENT.get();
        } catch (NullPointerException ignored) {
            return;
        }
        Set<Block> validBlocks = ((AccessorBlockEntityType) object).getValidBlocks();
        validBlocks = new ImmutableSet.Builder<Block>()
                .add(validBlocks.toArray(Block[]::new))
                .add(ventBlock)
                .build();
        ((AccessorBlockEntityType) object).setValidBlocks(validBlocks);
        registeredVentAsCopycat = true;
    }

    public static void addSignalSource(Block block) {
        if (registeredSignalSource) return;
        RegistryEntry<SignalDisplaySource> source = Railways.registrate().displaySource("track_signal_source", SignalDisplaySource::new).register();
        DisplaySource.BY_BLOCK.add(block, source.get());
        registeredSignalSource = true;
    }

    @ExpectPlatform
    public static void platformSpecificRegistration() {
        throw new AssertionError();
    }
}
