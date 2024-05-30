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

package com.railwayteam.railways.content.handcar;

import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.util.AdventureUtils;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsInteractionBehaviour;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class HandcarControlsInteractionBehaviour extends ControlsInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        if (!AdventureUtils.isAdventure(player) && AllItems.WRENCH.isIn(player.getItemInHand(activeHand))) {
            if (contraptionEntity instanceof CarriageContraptionEntity cce) {
                if (player.level.isClientSide) return true;

                ItemStack stack = CRBlocks.HANDCAR.asStack();
                if (!player.isCreative()) {
                    player.getInventory().placeItemBackInInventory(stack);
                }
                AllSoundEvents.WRENCH_REMOVE.playOnServer(player.level, new BlockPos(cce.toGlobalVector(Vec3.atCenterOf(localPos), 0.5f)), 1, player.getRandom().nextFloat() * .5f + .5f);

                TrainUtils.discardTrain(cce.getCarriage().train);
                return true;
            }
        }
        return super.handlePlayerInteraction(player, activeHand, localPos, contraptionEntity);
    }
}
