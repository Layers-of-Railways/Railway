package com.railwayteam.railways.blocks;

import com.railwayteam.railways.capabilities.CapabilitySetup;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StationSensorRailBlock extends DetectorRailBlock {
  public static final String name = "station_sensor";

  public StationSensorRailBlock (Properties props) {
    super (props);
  }

  @Override
  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof MinecartEntity) {
      entityIn.getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability -> {
        if (capability.getEntry().isEmpty()) return;
        super.onEntityCollision(state, worldIn, pos, entityIn);
      });
    }
  }

  @Override
  public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
    super.onMinecartPass(state, world, pos, cart);
  }
}
