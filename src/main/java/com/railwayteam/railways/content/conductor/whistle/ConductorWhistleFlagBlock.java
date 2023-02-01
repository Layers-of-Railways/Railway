package com.railwayteam.railways.content.conductor.whistle;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ConductorWhistleFlagBlock extends Block implements ITE<ConductorWhistleFlagTileEntity> {
    public ConductorWhistleFlagBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<ConductorWhistleFlagTileEntity> getTileEntityClass() {
        return ConductorWhistleFlagTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends ConductorWhistleFlagTileEntity> getTileEntityType() {
        return CRBlockEntities.CONDUCTOR_WHISTLE_FLAG.get();
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        ITE.onRemove(pState, pLevel, pPos, pNewState);
    }
}
