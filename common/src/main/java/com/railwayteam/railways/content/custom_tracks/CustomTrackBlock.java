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

package com.railwayteam.railways.content.custom_tracks;

import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.AdventureUtils;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.trains.track.TrackPropagator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CustomTrackBlock  { //done using a brass hand on a track should call TrackPropagator.onRailAdded to update materials
    @Nullable
    public static InteractionResult casingUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (AdventureUtils.isAdventure(player))
            return InteractionResult.FAIL;
        ItemStack handStack = player.getItemInHand(hand);
        if (AllItems.BRASS_HAND.isIn(handStack)) {
            TrackPropagator.onRailAdded(world, pos, state);
            return InteractionResult.SUCCESS;
        }
        if (handStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock slabBlock &&
            !CRTags.AllBlockTags.TRACK_CASING_BLACKLIST.matches(slabBlock)) {
            if (world.isClientSide) return InteractionResult.SUCCESS;
            SlabBlock currentCasing = IHasTrackCasing.getTrackCasing(world, pos);
            if (currentCasing == slabBlock) {
                return (IHasTrackCasing.setAlternateModel(world, pos, !IHasTrackCasing.isAlternate(world, pos))) ?
                    InteractionResult.SUCCESS : InteractionResult.FAIL;
            } else {
                if (!player.isCreative()) {
                    handStack.shrink(1);
                    player.setItemInHand(hand, handStack);
                    if (currentCasing != null) {
                        ItemStack casingStack = new ItemStack(currentCasing);
                        EntityUtils.givePlayerItem(player, casingStack);
                    }
                }
                IHasTrackCasing.setTrackCasing(world, pos, slabBlock);
            }
            return InteractionResult.SUCCESS;
        } else if (handStack.isEmpty()) {
            SlabBlock currentCasing = IHasTrackCasing.getTrackCasing(world, pos);
            if (currentCasing != null) {
                if (world.isClientSide) return InteractionResult.SUCCESS;
                handStack = new ItemStack(currentCasing);
                IHasTrackCasing.setTrackCasing(world, pos, null);
                if (!player.isCreative())
                    EntityUtils.givePlayerItem(player, handStack);
                return InteractionResult.SUCCESS;
            }
        }
        return null;
    }
}
