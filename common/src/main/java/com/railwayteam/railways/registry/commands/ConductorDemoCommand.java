/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ConductorDemoCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("conductor_demo")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(ctx -> {
                    BlockPos origin = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                    BlockPos.MutableBlockPos pos = origin.mutable();

                    Set<String> customCapNames = new HashSet<>();
                    customCapNames.addAll(CRBlockPartials.CUSTOM_CONDUCTOR_CAPS.keySet());
                    customCapNames.addAll(CRBlockPartials.CUSTOM_CONDUCTOR_SKINS.keySet());

                    Set<String> customConductorNames = CRBlockPartials.CUSTOM_CONDUCTOR_SKINS_FOR_NAME.keySet();

                    int i = 0;
                    for (String name : customCapNames) {
                        ItemStack capStack = CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.values()[i++ % DyeColor.values().length]).asStack();
                        capStack.setHoverName(Components.literal(name));

                        spawnConductor(ctx.getSource().getLevel(), pos, capStack, null);
                        pos.move(Direction.NORTH);
                    }

                    for (String name : customConductorNames) {
                        ItemStack capStack = CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.values()[i++ % DyeColor.values().length]).asStack();

                        spawnConductor(ctx.getSource().getLevel(), pos, capStack, name);
                        pos.move(Direction.NORTH);
                    }

                    ctx.getSource().sendSuccess(Components.literal("Spawned custom conductors"), true);
                    return 1;
                }));
    }

    private static void spawnConductor(Level level, BlockPos pos, ItemStack headStack, @Nullable String name) {
        ConductorEntity conductor = ConductorEntity.spawn(level, pos, headStack);
        if (conductor != null && name != null) {
            conductor.setCustomName(Components.literal(name));
            conductor.setCustomNameVisible(true);
        }
    }
}
