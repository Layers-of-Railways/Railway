package com.railwayteam.railways.content.minecarts;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class MinecartBlock extends AbstractMinecart {
  protected BlockState content;

  public MinecartBlock(EntityType<?> type, Level level, Block content) {
    super(type, level);
    this.content = content.defaultBlockState();
  }

  public MinecartBlock(EntityType<?> type, Level level, double x, double y, double z, Block content) {
    super(type, level, x, y, z);
    this.content = content.defaultBlockState();
  }

  @Override
  public BlockState getDisplayBlockState() {
    return content;
  }

  @Override
  protected @NotNull Item getDropItem() {
    return content.getBlock().asItem();
  }

  /*@Override
  public void destroy(DamageSource source) {
    super.destroy(source);
    if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
      this.spawnAtLocation(content.getBlock());
    }
  }*/
}
