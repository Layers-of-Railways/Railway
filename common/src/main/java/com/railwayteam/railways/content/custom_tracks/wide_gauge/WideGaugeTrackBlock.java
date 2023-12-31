package com.railwayteam.railways.content.custom_tracks.wide_gauge;

import com.railwayteam.railways.content.custom_tracks.NoCollisionCustomTrackBlock;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WideGaugeTrackBlock extends TrackBlock {
    public WideGaugeTrackBlock(Properties properties, TrackMaterial material) {
        super(properties, material);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (CRTrackMaterials.getBaseFromWide(getMaterial()).getBlock() instanceof NoCollisionCustomTrackBlock noCollisionBlock) {
            return noCollisionBlock.getCollisionShape(pState, pLevel, pPos, pContext);
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }
}
