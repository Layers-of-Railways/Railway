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
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.mixin.AccessorBlockEntityType;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlock;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.displayLink.DisplayBehaviour;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.EnumMap;
import java.util.Set;

public class CRExtraRegistration {
    private static boolean registeredSignalSource = false;
    private static boolean registeredVentAsCopycat = false;
    private static boolean registeredPalettesFlywheels = false;

    // register the source, working independently of mod loading order
    public static void register() {
        platformSpecificRegistration();
        addSignalSource();
    }

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
        if (Utils.isDevEnv()) {
            Railways.LOGGER.info("Registered vent as copycat");
        }
        registeredVentAsCopycat = true;
    }

    public static void addPalettesFlywheels(BlockEntityType<?> object) {
        if (registeredPalettesFlywheels) return;
        EnumMap<PalettesColor, FlywheelBlock> flywheels = new EnumMap<>(PalettesColor.class);
        try {
            for (PalettesColor color : PalettesColor.values()) {
                flywheels.put(color, (FlywheelBlock) CRPalettes.Styles.FLYWHEEL.get(color).get());
            }
        } catch (NullPointerException ignored) {
            return;
        }
        Set<Block> validBlocks = ((AccessorBlockEntityType) object).getValidBlocks();
        validBlocks = new ImmutableSet.Builder<Block>()
            .add(validBlocks.toArray(Block[]::new))
            .addAll(flywheels.values())
            .build();
        ((AccessorBlockEntityType) object).setValidBlocks(validBlocks);
        if (Utils.isDevEnv()) {
            Railways.LOGGER.info("Added palettes flywheels to BlockEntityType");
        }
        registeredPalettesFlywheels = true;
    }

    public static void addSignalSource() {
        if (registeredSignalSource) return;
        DisplayBehaviour signalDisplaySource = AllDisplayBehaviours.register(Create.asResource("track_signal_source"), new SignalDisplaySource());
        AllDisplayBehaviours.assignBlock(signalDisplaySource, Create.asResource("track_signal"));
        if (Utils.isDevEnv()) {
            Railways.LOGGER.info("Registered signal source");
        }
        registeredSignalSource = true;
    }

    @ExpectPlatform
    public static void platformSpecificRegistration() {
        throw new AssertionError();
    }
}
