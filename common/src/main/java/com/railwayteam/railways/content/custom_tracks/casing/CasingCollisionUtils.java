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

import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CasingCollisionUtils {
    private static final Map<TrackType, Map<TrackShape, Set<BlockPos>>> OFFSETS = new HashMap<>();

    private static class SuperBuilder {
        private final TrackType trackType;

        private SuperBuilder(TrackType trackType) {
            this.trackType = trackType;
        }

        public Builder s(TrackShape shape) {
            return new Builder(trackType, shape).withSuperBuilder(this);
        }
    }

    private static class Builder {
        private final Set<BlockPos> offsets = new HashSet<>();
        private final TrackType trackType;
        private final TrackShape shape;
        private SuperBuilder superBuilder;

        private Builder(TrackType trackType, TrackShape shape) {
            this.trackType = trackType;
            this.shape = shape;
        }

        public SuperBuilder build() {
            if (!OFFSETS.containsKey(trackType))
                OFFSETS.put(trackType, new HashMap<>());
            OFFSETS.get(trackType).put(shape, offsets);
            if (superBuilder == null)
                return new SuperBuilder(trackType);
            return superBuilder;
        }

/*        @Contract("_ -> new")
        public Builder shape(@NotNull TrackShape shape) {
            return new Builder(trackType, shape);
        }*/

        public Builder o(int x, int y, int z) {
            offsets.add(new BlockPos(x, y, z));
            return this;
        }

        public Builder o(int x, int z) {
            return o(x, 0, z);
        }

        public Builder withSuperBuilder(SuperBuilder superBuilder) {
            this.superBuilder = superBuilder;
            return this;
        }
    }

    @Contract("_ -> new")
    private static SuperBuilder b(@NotNull TrackType trackType) {
        return new SuperBuilder(trackType);
    }

    private static void registerStandard(TrackType trackType) {
        b(trackType)
                .s(TrackShape.XO)
                .o(0, 1)
                .o(0, -1)
                .build()
                .s(TrackShape.ZO)
                .o(1, 0)
                .o(-1, 0)
                .build()
                .s(TrackShape.PD)
                .o(1, 0)
                .o(1, -1)
                .o(0, 1)
                .o(-1, 1)
                .build()
                .s(TrackShape.ND)
                .o(-1, 0)
                .o(-1, -1)
                .o(0, 1)
                .o(1, 1)
                .build()
                .s(TrackShape.TE)
                .o(0, 1)
                .o(0, -1)
                .build()
                .s(TrackShape.TW)
                .o(0, 1)
                .o(0, -1)
                .build()
                .s(TrackShape.TN)
                .o(1, 0)
                .o(-1, 0)
                .build()
                .s(TrackShape.TS)
                .o(1, 0)
                .o(-1, 0)
                .build()
                .s(TrackShape.CR_O)
                .o(-1, 1).o(0, 1).o(1, 1)
                .o(-1, 0).o(1, 0)
                .o(-1, -1).o(0, -1).o(1, -1)
                .build()
                .s(TrackShape.AE)
                .o(0, 1)
                .o(0, -1)
                .build()
                .s(TrackShape.AW)
                .o(0, 1)
                .o(0, -1)
                .build()
                .s(TrackShape.AN)
                .o(1, 0)
                .o(-1, 0)
                .build()
                .s(TrackShape.AS)
                .o(1, 0)
                .o(-1, 0)
                .build()
        ;
    }

    private static void registerWide(TrackType trackType) {
        Builder cr_o = b(trackType)
                .s(TrackShape.XO)
                .o(0, 2)
                .o(0, 1)
                .o(0, -1)
                .o(0, -2)
                .build()
                .s(TrackShape.ZO)
                .o(2, 0)
                .o(1, 0)
                .o(-1, 0)
                .o(-2, 0)
                .build()
                .s(TrackShape.PD)
                .o(1, 0)
                .o(1, -1)
                .o(2, -1)
                .o(0, 1)
                .o(-1, 1)
                .o(-1, 2)
                .build()
                .s(TrackShape.ND)
                .o(-1, 0)
                .o(-1, -1)
                .o(-2, -1)
                .o(0, 1)
                .o(1, 1)
                .o(1, 2)
                .build()
                .s(TrackShape.TE)
                .o(0, 2)
                .o(0, 1)
                .o(0, -1)
                .o(0, -2)
                .build()
                .s(TrackShape.TW)
                .o(0, 2)
                .o(0, 1)
                .o(0, -1)
                .o(0, -2)
                .build()
                .s(TrackShape.TN)
                .o(2, 0)
                .o(1, 0)
                .o(-1, 0)
                .o(-2, 0)
                .build()
                .s(TrackShape.TS)
                .o(2, 0)
                .o(1, 0)
                .o(-1, 0)
                .o(-2, 0)
                .build()
                .s(TrackShape.AE)
                .o(0, 2)
                .o(0, 1)
                .o(0, -1)
                .o(0, -2)
                .build()
                .s(TrackShape.AW)
                .o(0, 2)
                .o(0, 1)
                .o(0, -1)
                .o(0, -2)
                .build()
                .s(TrackShape.AN)
                .o(2, 0)
                .o(1, 0)
                .o(-1, 0)
                .o(-2, 0)
                .build()
                .s(TrackShape.AS)
                .o(2, 0)
                .o(1, 0)
                .o(-1, 0)
                .o(-2, 0)
                .build()
                .s(TrackShape.CR_O);

        for (int xOff = -2; xOff <= 2; xOff++) {
            for (int zOff = -2; zOff <= 2; zOff++) {
                if (xOff == 0 && zOff == 0)
                    continue;
                cr_o.o(xOff, zOff);
            }
        }
        cr_o.build();
    }

    public static void register() {
        OFFSETS.clear();
        registerStandard(TrackType.STANDARD);
        registerStandard(CRTrackType.UNIVERSAL);
        registerStandard(CRTrackType.NARROW_GAUGE);
        registerWide(CRTrackType.WIDE_GAUGE);
    }

    public static boolean shouldMakeCollision(TrackBlockEntity be, BlockState state) {
        TrackShape shape = state.getValue(TrackBlock.SHAPE);
        if (((IHasTrackCasing) be).isAlternate() || ((IHasTrackCasing) be).getTrackCasing() == null)
            return false;
        TrackType trackType = ((TrackBlock) state.getBlock()).getMaterial().trackType;
        if (!OFFSETS.containsKey(trackType))
            return false;
        Map<TrackShape, Set<BlockPos>> shapeMap = OFFSETS.get(trackType);
        return shapeMap.containsKey(shape);
    }

    public static void manageTracks(TrackBlockEntity be, boolean remove) {
        TrackShape shape = be.getBlockState().getValue(TrackBlock.SHAPE);
        TrackType trackType = ((TrackBlock) be.getBlockState().getBlock()).getMaterial().trackType;
        if (!OFFSETS.containsKey(trackType))
            return;
        Map<TrackShape, Set<BlockPos>> shapeMap = OFFSETS.get(trackType);
        if (!shapeMap.containsKey(shape))
            return;

        Level level = be.getLevel();
        if (level == null)
            return;
        BlockPos pos;
        for (BlockPos offset : shapeMap.get(shape)) {
            pos = be.getBlockPos().offset(offset);

            BlockState stateAtPos = level.getBlockState(pos);
            boolean present = CRBlocks.CASING_COLLISION.has(stateAtPos);

            if (remove) {
                if (present)
                    level.removeBlock(pos, false);
                continue;
            }

            FluidState fluidState = stateAtPos.getFluidState();
            if (!fluidState.isEmpty() && !fluidState.isSourceOfType(Fluids.WATER))
                continue;

            if (!present && stateAtPos.getMaterial()
                    .isReplaceable())
                level.setBlock(pos,
                        ProperWaterloggedBlock.withWater(level, CRBlocks.CASING_COLLISION.getDefaultState(), pos), 3);
            CasingCollisionBlock.keepAlive(level, pos);
        }
    }
}
