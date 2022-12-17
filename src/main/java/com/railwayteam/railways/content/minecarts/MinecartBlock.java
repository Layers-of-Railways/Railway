package com.railwayteam.railways.content.minecarts;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
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

  @Override
  public void destroy(DamageSource source) {
    super.destroy(source);
    if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
      this.spawnAtLocation(content.getBlock());
    }
  }

  @Override
  protected Item getDropItem() {
    return null;
  }
}
