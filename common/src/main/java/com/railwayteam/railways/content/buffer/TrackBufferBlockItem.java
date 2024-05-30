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

package com.railwayteam.railways.content.buffer;


import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrackBufferBlockItem extends TrackTargetingBlockItem {

    public static <T extends Block> NonNullBiFunction<? super T, Properties, TrackTargetingBlockItem> ofType(EdgePointType<?> type) {
        return (b, p) -> new TrackBufferBlockItem(b, p, type);
    }

    public TrackBufferBlockItem(Block pBlock, Properties pProperties, EdgePointType<?> type) {
        super(pBlock, pProperties, type);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(@NotNull BlockPlaceContext context) {
        if (context instanceof BufferBlockPlaceContext bufferContext && bufferContext.overrideBlock != null) {
            BlockState blockState = bufferContext.overrideBlock.getStateForPlacement(context);
            return blockState != null && this.canPlace(context, blockState) ? blockState : null;
        }
        return super.getPlacementState(context);
    }

    private static boolean isOkShape(BlockState state) {
        TrackShape shape = state.getValue(TrackBlock.SHAPE);
        return switch (shape) {
            case ZO, XO,
                TE, TN, TS, TW -> true;
            default -> false;
        };
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;

        if (state.getBlock() instanceof ITrackBlock track) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;

            Vec3 lookAngle = player.getLookAngle();
            Pair<Vec3, AxisDirection> nearestTrackAxis = track.getNearestTrackAxis(level, pos, state, lookAngle);
            boolean front = nearestTrackAxis.getSecond() == AxisDirection.POSITIVE;
            Axis axis = Direction.getNearest(nearestTrackAxis.getFirst().x, nearestTrackAxis.getFirst().y, nearestTrackAxis.getFirst().z).getAxis();
            EdgePointType<?> type = getType(stack);

            MutableObject<OverlapResult> result = new MutableObject<>(null);
            withGraphLocation(level, pos, front, null, type, (overlap, location) -> result.setValue(overlap));

            if (result.getValue().feedback != null) {
                player.displayClientMessage(Lang.translateDirect(result.getValue().feedback)
                    .withStyle(ChatFormatting.RED), true);
                AllSoundEvents.DENY.play(level, null, pos, .5f, 1);
                return InteractionResult.FAIL;
            }
            if (!isOkShape(state)) {
                player.displayClientMessage(Components.translatable("railways.buffer.invalid_shape")
                    .withStyle(ChatFormatting.RED), true);
                AllSoundEvents.DENY.play(level, null, pos, .5f, 1);
                return InteractionResult.FAIL;
            }

            CompoundTag stackTag = stack.getOrCreateTag();
            stack.setTag(stackTag);

            CompoundTag oldTeTag = stackTag.getCompound("BlockEntityTag");

            CompoundTag teTag = new CompoundTag();
            if (oldTeTag != null) {
                if (oldTeTag.contains("Material", Tag.TAG_COMPOUND))
                    //noinspection DataFlowIssue
                    teTag.put("Material", oldTeTag.get("Material"));
                if (oldTeTag.contains("Color", Tag.TAG_INT))
                    //noinspection DataFlowIssue
                    teTag.put("Color", oldTeTag.get("Color"));
            }
            teTag.putBoolean("TargetDirection", front);

            BlockPos placedPos = pos.above();
            Direction placeDirection = Direction.UP;

            TrackBufferBlock<?> overrideBlock = null;
            if (track.getMaterial().trackType == CRTrackMaterials.CRTrackType.NARROW_GAUGE) {
                overrideBlock = CRBlocks.TRACK_BUFFER_NARROW.get();
            } else if (track.getMaterial().trackType == CRTrackMaterials.CRTrackType.WIDE_GAUGE) {
                overrideBlock = CRBlocks.TRACK_BUFFER_WIDE.get();
            } else if (track.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
                overrideBlock = CRBlocks.TRACK_BUFFER_MONO.get();
                placedPos = context.getClickedFace() == Direction.DOWN ? pos.below() : pos.above();
                placeDirection = context.getClickedFace();
            }

            teTag.put("TargetTrack", NbtUtils.writeBlockPos(pos.subtract(placedPos)));
            stackTag.put("BlockEntityTag", teTag);

            InteractionResult useOn = place(BufferBlockPlaceContext.at(
                new BlockPlaceContext(context), placedPos, placeDirection,
                Direction.fromAxisAndDirection(axis, nearestTrackAxis.getSecond()), overrideBlock
            ));

            teTag.remove("TargetTrack");
            teTag.remove("TargetDirection");

            return useOn;
        }

        return InteractionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean useOnCurve(TrackBlockOutline.BezierPointSelection selection, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = mc.level;

        if (player != null) {

            player.displayClientMessage(Lang.translateDirect("track_target.invalid")
                    .withStyle(ChatFormatting.RED), true);
            AllSoundEvents.DENY.play(level, player, player.position(), .5f, 1);
            return false;
        }
        return false;
    }
}