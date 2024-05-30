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

package com.railwayteam.railways.content.custom_tracks.generic_crossing;

import com.railwayteam.railways.content.custom_tracks.generic_crossing.TrackShapeLookup.GenericCrossingData;
import com.railwayteam.railways.mixin_interfaces.IGenericCrossingTrackBE;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.blockEntity.IMergeableBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericCrossingBlockEntity extends SmartBlockEntity implements IMergeableBE, IGenericCrossingTrackBE {

    boolean cancelDrops = false;
    private Couple<TrackMaterial> materials = null;

    public GenericCrossingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(100);
    }

    @NotNull
    public TrackMaterial getPrimary() {
        if (materials == null)
            return TrackMaterial.ANDESITE;
        return materials.getFirst() == null ? TrackMaterial.ANDESITE : materials.getFirst();
    }

    @NotNull
    public TrackMaterial getSecondary() {
        if (materials == null)
            return TrackMaterial.ANDESITE;
        return materials.getSecond() == null ? TrackMaterial.ANDESITE : materials.getSecond();
    }

    @Override
    public void accept(BlockEntity other) {
        level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);

        boolean updateMesh = false;
        TrackMaterial primary = TrackMaterial.deserialize(tag.getString("PrimaryMaterial"));
        TrackMaterial secondary = TrackMaterial.deserialize(tag.getString("SecondaryMaterial"));

        if (primary != getPrimary() || secondary != getSecondary()) updateMesh = true;

        materials = Couple.create(primary, secondary);

        if (clientPacket && updateMesh)
            redraw();
    }

    private void redraw() {
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        tag.putString("PrimaryMaterial", getPrimary().id.toString());
        tag.putString("SecondaryMaterial", getSecondary().id.toString());
    }

    @Override
    public @Nullable Pair<TrackMaterial, TrackShape> railways$getFirstCrossingPiece() {
        TrackMaterial primary = getPrimary();
        Couple<TrackShape> unmerged = TrackShapeLookup.getUnmerged(getBlockState().getValue(GenericCrossingBlock.SHAPE));
        if (unmerged == null) return null;

        return Pair.of(primary, unmerged.getFirst());
    }

    @Override
    public @Nullable Pair<TrackMaterial, TrackShape> railways$getSecondCrossingPiece() {
        TrackMaterial secondary = getSecondary();
        Couple<TrackShape> unmerged = TrackShapeLookup.getUnmerged(getBlockState().getValue(GenericCrossingBlock.SHAPE));
        if (unmerged == null) return null;

        return Pair.of(secondary, unmerged.getSecond());
    }

    public void initFrom(GenericCrossingData crossingData) {
        if (level.isClientSide) return;
        boolean flip = crossingData.merged().getSecond();
        TrackMaterial primary = flip ? crossingData.overlayMaterial() : crossingData.existingMaterial();
        TrackMaterial secondary = flip ? crossingData.existingMaterial() : crossingData.overlayMaterial();
        materials = Couple.create(primary, secondary);
        notifyUpdate();
    }
}
