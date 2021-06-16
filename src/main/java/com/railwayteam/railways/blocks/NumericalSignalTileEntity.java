package com.railwayteam.railways.blocks;

import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class NumericalSignalTileEntity extends TileEntity {
    public NumericalSignalTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    public int getPower() {
        return getWorld().getBlockState(getPos()).get(BlockStateProperties.POWER_0_15);
    }
}
