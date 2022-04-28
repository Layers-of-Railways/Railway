package com.railwayteam.railways.content.minecarts;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartBlock extends AbstractMinecart {
  protected BlockState content;

  public MinecartBlock (EntityType<?> type, Level level, Block content) {
    super(type, level);
    this.content = content.defaultBlockState();
  }

  @Override
  public Type getMinecartType() {
    return null;
  }

  @Override
  public BlockState getDisplayBlockState() {
    return content;
  }

  @Override
  public boolean canBeRidden() {
    return false;
  }
}
