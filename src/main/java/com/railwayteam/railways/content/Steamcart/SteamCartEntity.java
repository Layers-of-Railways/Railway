package com.railwayteam.railways.content.Steamcart;

import com.railwayteam.railways.content.minecarts.MinecartBlock;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SteamCartEntity extends MinecartBlock {
  private boolean powered;
  private int toggleCooldown, fuel, water;
  private static final int COOLDOWN = 100; // ticks

  protected Vec3 directionCache;

  public SteamCartEntity (EntityType<?> type, Level level) {
    super(type, level, CRBlocks.BLOCK_STEAMCART.get());
    powered = false;
    toggleCooldown = COOLDOWN;
    directionCache = Vec3.ZERO;
    fuel = 0;
    water = 0;
  }

  public boolean isPowered () {
    return powered;
  }

  @Override
  public Type getMinecartType() {
    return Type.FURNACE;
  }

  @Override
  public boolean isVehicle() {
    return false; // prevents entities from riding us
  }

  @Override
  public boolean canBeRidden() {
    return true; // Contraptions check this
  }

  @Override
  public void tick () {
    super.tick();
    if (toggleCooldown > 0) toggleCooldown--;

    if (this.powered) {
      if (this.random.nextInt(4) == 0) {
        this.level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getX(), this.getY() + 0.8D, this.getZ(), 0.0D, 0.0D, 0.0D);
      }
    }
  }

  @Override
  protected void moveAlongTrack(BlockPos pos, BlockState state) {
    super.moveAlongTrack(pos, state);
    directionCache = this.getDeltaMovement();
  }

  @Override
  protected void applyNaturalSlowdown() {
    if (!powered) super.applyNaturalSlowdown();
  }

  @Override
  public void activateMinecart(int x, int y, int z, boolean active) {
    if ((toggleCooldown <= 0) && active) {
      toggleCartRunning();
    }
  }

  @Override
  public ItemStack getPickResult() {
    return CRItems.ITEM_STEAMCART.asStack();
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
