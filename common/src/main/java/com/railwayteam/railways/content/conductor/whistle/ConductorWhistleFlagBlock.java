package com.railwayteam.railways.content.conductor.whistle;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ConductorWhistleFlagBlock extends Block implements IBE<ConductorWhistleFlagBlockEntity> {
    public ConductorWhistleFlagBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<ConductorWhistleFlagBlockEntity> getBlockEntityClass() {
        return ConductorWhistleFlagBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ConductorWhistleFlagBlockEntity> getBlockEntityType() {
        return CRBlockEntities.CONDUCTOR_WHISTLE_FLAG.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return CRShapes.CONDUCTOR_WHISTLE_FLAG;
    }
}
