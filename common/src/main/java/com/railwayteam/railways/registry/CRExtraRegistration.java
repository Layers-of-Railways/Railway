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
import com.railwayteam.railways.base.registration.MultiRegistryCallback;
import com.railwayteam.railways.content.distant_signals.SignalDisplaySource;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.mixin.AccessorBlockEntityType;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.displayLink.DisplayBehaviour;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashSet;
import java.util.Set;

public class CRExtraRegistration {
    private static boolean registeredSignalSource = false;
    private static Set<BlockEntityType<?>> modifiedTypes = new HashSet<>();

    // register the source, working independently of mod loading order
    public static void register() {
        addSignalSource();
        addVentAsCopycat();
        addPalettesBlocks();
        MultiRegistryCallback.addFinalizer(CRExtraRegistration::finalizeBlockEntityTypes);
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

    private static void addVentAsCopycat() {
        addRailwaysBlockToCreateBlockEntity(CRBlocks.CONDUCTOR_VENT, Create.asResource("copycat"));
    }

    private static void addPalettesBlocks() {
        for (PalettesColor color : PalettesColor.values()) {
            addRailwaysBlockToCreateBlockEntity(CRPalettes.Styles.FLYWHEEL.get(color), Create.asResource("flywheel"));
            addRailwaysBlockToCreateBlockEntity(CRPalettes.Styles.SLIDING_DOOR.get(color), Create.asResource("sliding_door"));
            addRailwaysBlockToCreateBlockEntity(CRPalettes.Styles.FOLDING_DOOR.get(color), Create.asResource("sliding_door"));
        }
    }

    private static void addRailwaysBlockToCreateBlockEntity(BlockEntry<?> railwaysBlock, ResourceLocation createBE) {
        MultiRegistryCallback.create(
            Create.REGISTRATE, Registries.BLOCK_ENTITY_TYPE, createBE,
            Railways.registrate(), Registries.BLOCK, railwaysBlock.getId(),
            CRExtraRegistration::addBlockToBE
        );
    }

    private static void addBlockToBE(BlockEntityType<?> be, Block block) {
        Set<Block> validBlocks = ((AccessorBlockEntityType) be).getValidBlocks();
        try {
            validBlocks.add(block);
        } catch (UnsupportedOperationException e) {
            validBlocks = new HashSet<>(validBlocks);
            validBlocks.add(block);
            ((AccessorBlockEntityType) be).setValidBlocks(validBlocks);
        }

        if (modifiedTypes != null) {
            modifiedTypes.add(be);
        } else {
            Railways.LOGGER.warn("Added valid block ({}) to block entity type ({}) after finalization. Refreezing will be skipped. This may incur a performance penalty.", block, be);
        }
    }

    // it is likely that an ImmutableSet will be more efficient in terms of memory and query time than a HashSet, so we
    // refreeze the set after all modifications are done
    private static void finalizeBlockEntityTypes() {
        for (BlockEntityType<?> type : modifiedTypes) {
            Set<Block> validBlocks = ((AccessorBlockEntityType) type).getValidBlocks();
            if (!(validBlocks instanceof HashSet)) continue;
            ((AccessorBlockEntityType) type).setValidBlocks(ImmutableSet.copyOf(validBlocks));
        }
        modifiedTypes.clear();
        modifiedTypes = null;
    }
}
