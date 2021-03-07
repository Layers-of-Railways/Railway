package com.railwayteam.railways.blocks;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.capabilities.CapabilitySetup;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

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
  public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    if (!worldIn.isRemote) {
      TileEntity tile = worldIn.getTileEntity(pos);
      if (!(tile instanceof StationSensorRailTileEntity)) return ActionResultType.PASS;
    //  LogManager.getLogger(Railways.MODID).debug("found TE: " + ((StationSensorRailTileEntity)tile).NAME);
      String candidate = player.getDisplayName().getFormattedText();
      ((StationSensorRailTileEntity)tile).setStation(candidate);
      if ( ((StationSensorRailTileEntity)tile).getStation().equals(candidate) ) {
        player.sendMessage(new StringTextComponent("station already assigned"));
      }
      else player.sendMessage(new StringTextComponent("assigned station: " + candidate));
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }

  @Override
  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof MinecartEntity) {
      entityIn.getCapability(CapabilitySetup.CAPABILITY_STATION_LIST).ifPresent(capability -> {
        if (capability.getEntry().isEmpty()) return;
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof StationSensorRailTileEntity)) return;
        if (capability.getEntry().equals( ((StationSensorRailTileEntity)te).getStation() )) {
          super.onEntityCollision(state, worldIn, pos, entityIn);
        }
      });
    }
  }

  @Override
  public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
    super.onMinecartPass(state, world, pos, cart);
  }
}
