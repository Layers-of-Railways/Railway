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

package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(value = TrackTargetingBehaviour.class, remap = false)
public interface AccessorTrackTargetingBehavior {
    @Accessor
    UUID getId();

    @Accessor
    void setId(UUID id);

    @Accessor
    BlockPos getTargetTrack();

    @Accessor
    void setTargetTrack(BlockPos targetTrack);

    @Accessor
    boolean isOrthogonal();

    @Accessor
    void setOrthogonal(boolean orthogonal);

    @Accessor
    Direction.AxisDirection getTargetDirection();

    @Accessor
    void setTargetDirection(Direction.AxisDirection targetDirection);

    @Accessor
    Vec3 getRotatedDirection();

    @Accessor
    void setRotatedDirection(Vec3 rotatedDirection);

    @Accessor
    Vec3 getPrevDirection();

    @Accessor
    void setPrevDirection(Vec3 prevDirection);

    @Accessor
    CompoundTag getMigrationData();

    @Accessor
    void setMigrationData(CompoundTag migrationData);

    @Accessor
    BezierTrackPointLocation getTargetBezier();

    @Accessor
    void setTargetBezier(BezierTrackPointLocation targetBezier);

    @Accessor
    void setEdgePoint(TrackEdgePoint edgePoint);
}
