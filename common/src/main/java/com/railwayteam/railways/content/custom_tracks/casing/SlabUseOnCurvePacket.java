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

package com.railwayteam.railways.content.custom_tracks.casing;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.util.AdventureUtils;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SlabUseOnCurvePacket implements C2SPacket {

    private BlockPos pos;
    private BlockPos targetPos;
    private BlockPos soundSource;

    public SlabUseOnCurvePacket(BlockPos pos, BlockPos targetPos, BlockPos soundSource) {
        this.pos = pos;
        this.targetPos = targetPos;
        this.soundSource = soundSource;
    }

    public SlabUseOnCurvePacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        targetPos = buffer.readBlockPos();
        soundSource = buffer.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBlockPos(targetPos);
        buffer.writeBlockPos(soundSource);
    }

    @Override
    public void handle(ServerPlayer player) {
        Level world = player.level;
        if (AdventureUtils.isAdventure(player))
            return;
        if (!world.isLoaded(pos))
            return;
        if (!pos.closerThan(player.blockPosition(), 64))
            return;
        BlockEntity BlockEntity = world.getBlockEntity(pos);
        if (BlockEntity instanceof TrackBlockEntity track) {
            applySettings(player, track);
            track.notifyUpdate();
        }
    }

    private InteractionResult useOn(ServerPlayer player, InteractionHand hand, Level world, IHasTrackCasing casingAble) {
        if (world.isClientSide) return InteractionResult.FAIL;
        ItemStack handStack = player.getItemInHand(hand);
        if (handStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock slabBlock) {
            SlabBlock currentCasing = casingAble.getTrackCasing();
            if (currentCasing == slabBlock) {
                casingAble.setAlternate(!casingAble.isAlternate());
                return InteractionResult.SUCCESS;
            } else {
                if (!player.isCreative()) {
                    handStack.shrink(1);
                    if (currentCasing != null) {
                        ItemStack casingStack = new ItemStack(currentCasing);
                        EntityUtils.givePlayerItem(player, casingStack);
                    }
                    player.setItemInHand(hand, handStack);
                }
                casingAble.setTrackCasing(slabBlock);
            }
            return InteractionResult.SUCCESS;
        } else if (handStack.isEmpty()) {
            SlabBlock currentCasing = casingAble.getTrackCasing();
            if (currentCasing != null) {
                handStack = new ItemStack(currentCasing);
                casingAble.setTrackCasing(null);
                if (!player.isCreative())
                    EntityUtils.givePlayerItem(player, handStack);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    protected void applySettings(ServerPlayer player, TrackBlockEntity te) {
        if (!te.getBlockPos()
                .closerThan(player.blockPosition(), 128)) {
            Railways.LOGGER.warn(player.getScoreboardName() + " too far away from slabbed Curve track");
            return;
        }

        Level level = te.getLevel();
        BezierConnection bezierConnection = te.getConnections()
                .get(targetPos);

        if (bezierConnection.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            Railways.LOGGER.warn(player.getScoreboardName() + "tried to slab a monorail track");
            return;
        }

        if (level != null) {
            InteractionHand hand = InteractionHand.MAIN_HAND;
            InteractionResult result = useOn(player, hand, level, (IHasTrackCasing) bezierConnection);
            if (!result.consumesAction()) {
                hand = InteractionHand.OFF_HAND;
                result = useOn(player, hand, level, (IHasTrackCasing) bezierConnection);
            }
            if (result.shouldSwing())
                player.swing(hand, true);

            te.notifyUpdate();
        }
    }
}
