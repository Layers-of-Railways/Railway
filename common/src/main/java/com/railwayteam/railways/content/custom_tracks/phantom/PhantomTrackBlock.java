package com.railwayteam.railways.content.custom_tracks.phantom;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_tracks.NoCollisionCustomTrackBlock;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PhantomTrackBlock extends NoCollisionCustomTrackBlock {
    public PhantomTrackBlock(Properties properties, TrackMaterial material) {
        super(properties, material);
    }

    @Override
    public PartialModel prepareTrackOverlay(BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint, Direction.AxisDirection direction, PoseStack ms, TrackTargetingBehaviour.RenderedTrackOverlayType type) {
        if (bezierPoint == null && !PhantomSpriteManager.isVisible())
            return null;
        return super.prepareTrackOverlay(world, pos, state, bezierPoint, direction, ms, type);
    }
}
