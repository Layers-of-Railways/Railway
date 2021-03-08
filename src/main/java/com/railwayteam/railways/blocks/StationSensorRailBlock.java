package com.railwayteam.railways.blocks;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.capabilities.CapabilitySetup;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class StationSensorRailBlock extends DetectorRailBlock {
  public static final String name = "station_sensor";

  public StationSensorRailBlock (Properties props) {
    super (props);
  }

  @Override
  public boolean hasTileEntity (final BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity (final BlockState state, final IBlockReader world) {
    return ModSetup.R_TE_STATION_SENSOR.create();
  }

  @Override
  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof MinecartEntity) {
      entityIn.getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability -> {
        if (capability.isEmpty()) return;
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof StationSensorRailTileEntity)) return;
        if (capability.contains( ((StationSensorRailTileEntity)te).getStation() )) {
          super.onEntityCollision(state, worldIn, pos, entityIn);
        }
      });
    }
  }
}
