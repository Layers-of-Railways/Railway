package com.railwayteam.railways.blocks;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.capabilities.CapabilitySetup;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Iterator;

import net.minecraft.block.AbstractBlock.Properties;

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
  public void onPlace(BlockState blockstate, World world, BlockPos pos, BlockState oldstate, boolean p_220082_5_) {
    super.onPlace(blockstate, world, pos, oldstate, p_220082_5_);
    ((StationSensorRailTileEntity)world.getBlockEntity(pos)).setStation(pos.toShortString());
  }

  @Override
  public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof MinecartEntity) {
      Railways.LOGGER.debug("minecart detected...");
      entityIn.getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability -> {
        Railways.LOGGER.debug("  capability is present...");
        if (capability.isEmpty()) return;
        Railways.LOGGER.debug("    capability is not empty...");
        TileEntity te = worldIn.getBlockEntity(pos);
        if (!(te instanceof StationSensorRailTileEntity)) return;
        Railways.LOGGER.debug("sanity secure, checking contents vs '" + ((StationSensorRailTileEntity)te).getStation() + "'...");
        Iterator<String> iter = capability.iterate();
        while (iter.hasNext()) Railways.LOGGER.debug("    " + iter.next());
        if (capability.contains(( "(" + ((StationSensorRailTileEntity)te).getStation() + ")" ).replace(" ",""))) {
          Railways.LOGGER.debug("  found a hit");
          super.entityInside(state, worldIn, pos, entityIn);
        }
      });
    }
  }
}
