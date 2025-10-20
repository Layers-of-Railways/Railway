/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.railwayteam.railways.content.moving_bes.GuiBlockMovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CRInteractionBehaviours {
    public static void register() {
        add(Blocks.CARTOGRAPHY_TABLE, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.CRAFTING_TABLE, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.GRINDSTONE, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.LOOM, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.SMITHING_TABLE, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.STONECUTTER, new GuiBlockMovingInteractionBehaviour());
    }

    private static void add(Block block, MovingInteractionBehaviour behaviour) {
        MovingInteractionBehaviour.REGISTRY.register(block, behaviour);
    }
}
