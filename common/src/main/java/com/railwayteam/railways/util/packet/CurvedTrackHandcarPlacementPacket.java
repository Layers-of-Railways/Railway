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

package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.handcar.HandcarItem;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.mutable.MutableObject;

public class CurvedTrackHandcarPlacementPacket implements C2SPacket {

    private final BlockPos pos;
    private final BlockPos targetPos;
    private final boolean front;
    private final int segment;
    private final int slot;

    public CurvedTrackHandcarPlacementPacket(BlockPos pos, BlockPos targetPos, int segment, boolean front, int slot) {
        this.pos = pos;
        this.targetPos = targetPos;
        this.segment = segment;
        this.front = front;
        this.slot = slot;
    }

    public CurvedTrackHandcarPlacementPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        targetPos = buf.readBlockPos();
        segment = buf.readVarInt();
        front = buf.readBoolean();
        slot = buf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBlockPos(targetPos);
        buffer.writeVarInt(segment);
        buffer.writeBoolean(front);
        buffer.writeVarInt(slot);
    }

    protected void actuallyHandle(ServerPlayer player, TrackBlockEntity be) {
        if (player.getInventory().selected != slot)
            return;
        ItemStack stack = player.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof HandcarItem handcarItem))
            return;

        MutableObject<TrackTargetingBlockItem.OverlapResult> result = new MutableObject<>(null);
        MutableObject<TrackGraphLocation> resultLoc = new MutableObject<>(null);
        HandcarItem.withGraphLocation(player.level, pos, front,
            new BezierTrackPointLocation(targetPos, segment), (overlap, location) -> {
                result.setValue(overlap);
                resultLoc.setValue(location);
            });

        BezierConnection bc = be.getConnections().get(targetPos);
        TrackMaterial.TrackType trackType = bc.getMaterial().trackType;
        if (!(trackType == TrackMaterial.TrackType.STANDARD || trackType == CRTrackMaterials.CRTrackType.UNIVERSAL))
            return;

        if (result.getValue().feedback != null) {
            player.displayClientMessage(Lang.translateDirect(result.getValue().feedback)
                .withStyle(ChatFormatting.RED), true);
            AllSoundEvents.DENY.play(player.level, null, pos, .5f, 1);
            return;
        }

        TrackGraphLocation loc = resultLoc.getValue();
        if (loc == null)
            return;

        if (handcarItem.placeHandcar(loc, player.level, player, pos)) {
            if (!player.isCreative())
                stack.shrink(1);
        }
    }

    @Override
    public void handle(ServerPlayer sender) {
        Level world = sender.level;
        if (world == null || !world.isLoaded(pos))
            return;
        if (!pos.closerThan(sender.blockPosition(), 64))
            return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TrackBlockEntity tbe) {
            actuallyHandle(sender, tbe);
        }
    }
}
