package com.railwayteam.railways.content.tiles.tiles;

import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class SpeedSignalTileEntity extends TileEntity {
    public SpeedSignalTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    public int getPower() {
        return getWorld().getBlockState(getPos()).get(BlockStateProperties.POWER_0_15);
    }
}
