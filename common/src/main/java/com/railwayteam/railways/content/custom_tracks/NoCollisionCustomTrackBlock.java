package com.railwayteam.railways.content.custom_tracks;

import com.railwayteam.railways.track_api.TrackMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NoCollisionCustomTrackBlock extends CustomTrackBlock {
    public NoCollisionCustomTrackBlock(Properties properties, TrackMaterial material) {
        super(properties.noCollission(), material);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }
}
