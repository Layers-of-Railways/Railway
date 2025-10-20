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

package com.railwayteam.railways.content.custom_tracks.phantom;

import com.railwayteam.railways.content.custom_tracks.NoCollisionCustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.TransparentSegmentTrackBlock;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PhantomTrackBlock extends NoCollisionCustomTrackBlock implements TransparentSegmentTrackBlock {
    public PhantomTrackBlock(Properties properties, TrackMaterial material) {
        super(properties, material);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <Self extends Affine<Self>> PartialModel prepareTrackOverlay(Affine<Self> affine, BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint, AxisDirection direction, RenderedTrackOverlayType type) {
        if (bezierPoint == null && !PhantomSpriteManager.isVisible())
            return null;
        return super.prepareTrackOverlay(affine, world, pos, state, bezierPoint, direction, type);
    }
}
