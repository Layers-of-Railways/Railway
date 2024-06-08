/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
