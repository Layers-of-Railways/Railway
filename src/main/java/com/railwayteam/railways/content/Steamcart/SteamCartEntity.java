package com.railwayteam.railways.content.Steamcart;

import com.railwayteam.railways.content.minecarts.MinecartBlock;
import com.railwayteam.railways.registry.CRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SteamCartEntity extends MinecartBlock {
  private boolean powered;
  private int toggleCooldown;
  private static final int COOLDOWN = 100; // ticks

  protected Vec3 directionCache;

  public SteamCartEntity (EntityType<?> type, Level level) {
    super(type, level, CRBlocks.BLOCK_STEAMCART.get());
    powered = false;
    toggleCooldown = COOLDOWN;
    directionCache = Vec3.ZERO;
  }

  public boolean isPowered () {
    return powered;
  }

  @Override
  public Type getMinecartType() {
    return Type.FURNACE;
  }

  @Override
  public void tick () {
    super.tick();
    if (toggleCooldown > 0) toggleCooldown--;
  }

  @Override
  protected void moveAlongTrack(BlockPos pos, BlockState state) {
    super.moveAlongTrack(pos, state);
    directionCache = this.getDeltaMovement();
  }

  @Override
  protected void applyNaturalSlowdown() {
    if (!powered) {
      super.applyNaturalSlowdown();
    } else {
      
    }
  }

  @Override
  public void activateMinecart(int x, int y, int z, boolean active) {
    if ((toggleCooldown <= 0) && active) {
      toggleCartRunning();
    }
  }

  public void handleEntityEvent(byte data) {
    if (data == 10) {
      toggleCartRunning();
    }
  }

  public void toggleCartRunning () {
    powered = !powered;
    toggleCooldown = COOLDOWN;
    if (!level.isClientSide()) {
    //  Railways.LOGGER.error("toggled cart state");
      level.broadcastEntityEvent(this, (byte)10);
    }
  }

  public BlockState getDisplayBlockState () {
    boolean negative = getMotionDirection().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
    return content.setValue(SteamCartBlock.POWERED, isPowered()).setValue(SteamCartBlock.FACING, negative ? Direction.WEST : Direction.EAST);
  }
}
