package com.railwayteam.railways.content.Steamcart;

import com.railwayteam.railways.content.minecarts.MinecartBlock;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

public class SteamCartEntity extends MinecartBlock {
  private boolean powered;
  private int toggleCooldown, fuel, water;
  private static final int COOLDOWN = 100; // ticks
  private static final float BURN_RATIO = 3600f / ForgeHooks.getBurnTime(new ItemStack(Items.COAL), RecipeType.SMELTING); // 3600 ticks is from the Furnace Minecart

  protected Vec3 directionCache;

  public double xPush;
  public double zPush;

  public SteamCartEntity (EntityType<?> type, Level level) {
    super(type, level, CRBlocks.BLOCK_STEAMCART.get());
    powered = false;
    toggleCooldown = COOLDOWN;
    directionCache = Vec3.ZERO;
    fuel = 0;
    water = 0;
    xPush = 0d;
    zPush = 0d;
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
  public InteractionResult interact (Player player, InteractionHand hand) {
    InteractionResult ret = super.interact(player, hand);
    if (ret.consumesAction()) return ret;

    ItemStack stack = player.getItemInHand(hand);
    if (stack.is(ItemTags.COALS)  && fuel <= 32000) {
      fuel += Math.round(ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) * BURN_RATIO);
      if (!player.getAbilities().instabuild) stack.shrink(1);
    }
    else if (stack.getItem().equals(Items.WATER_BUCKET) && water <= 32000) {
      water += 3600;
      if (!player.getAbilities().instabuild) player.setItemInHand(hand, new ItemStack(Items.BUCKET));
      player.awardStat(Stats.ITEM_USED.get(Items.WATER_BUCKET));
    }

    if (fuel > 0 && water > 0) {
      this.xPush = this.getX() - player.getX();
      this.zPush = this.getZ() - player.getZ();
      if (!level.isClientSide()) setCartRunning(true);
    }
    return InteractionResult.sidedSuccess(this.level.isClientSide);
  }

  @Override
  public void tick () {
    super.tick();
    if (toggleCooldown > 0) toggleCooldown--;

    if (powered) {
      if (water > fuel) {
        --water;
      }
      else if (fuel > 0) {
        --fuel;
      }
      else { // fuel and water are <= 0
        setCartRunning(false);
        this.xPush = 0.0D;
        this.zPush = 0.0D;
      }

      if (this.random.nextInt(16) <= 3) {
        this.level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getX(), this.getY() + 0.8D, this.getZ(), 0.0D, 0.05D, 0.0D);
      }
    }
  }

  @Override
  protected void moveAlongTrack(BlockPos pos, BlockState state) {
    directionCache = this.getDeltaMovement();

    // taken from the Furnace Minecart
    double d0 = 1.0E-4D;
    double d1 = 0.001D;
    super.moveAlongTrack(pos, state);
    if (!isPowered()) return;
    Vec3 vec3 = this.getDeltaMovement();
    double d2 = vec3.horizontalDistanceSqr();
    double d3 = this.xPush * this.xPush + this.zPush * this.zPush;
    if (d3 > 1.0E-4D && d2 > 0.001D) {
      double d4 = Math.sqrt(d2);
      double d5 = Math.sqrt(d3);
      this.xPush = vec3.x / d4 * d5;
      this.zPush = vec3.z / d4 * d5;
    }
  }

  @Override
  protected void applyNaturalSlowdown() {
    if (isPowered()) {
      // taken from the Furnace Minecart
      double d0 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (d0 > 1.0E-7D) {
        d0 = Math.sqrt(d0);
        this.xPush /= d0;
        this.zPush /= d0;
        Vec3 vec3 = this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D).add(this.xPush, 0.0D, this.zPush);
        if (this.isInWater()) {
          vec3 = vec3.scale(0.1D);
        }

        this.setDeltaMovement(vec3);
      } else {
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.98D, 0.0D, 0.98D));
      }
    }
    super.applyNaturalSlowdown();
  }

  @Override
  public void activateMinecart(int x, int y, int z, boolean active) {
    if ((toggleCooldown <= 0) && active) {
      toggleCartRunning();
    }
  }

  // values taken from the Furnace Minecart
  @Override
  protected double getMaxSpeed() {
    return (this.isInWater() ? 3.0D : 4.0D) / 20.0D;
  }

  @Override
  public float getMaxCartSpeedOnRail() {
    return 0.2f;
  }

  @Override
  public ItemStack getPickResult() {
    return CRItems.ITEM_STEAMCART.asStack();
  }

  @Override
  public void destroy(DamageSource source) {
    this.remove(Entity.RemovalReason.KILLED);
    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
      ItemStack itemstack = new ItemStack(CRItems.ITEM_STEAMCART.get());
      if (this.hasCustomName()) {
        itemstack.setHoverName(this.getCustomName());
      }
      spawnAtLocation(itemstack);
    }
  }

  public void handleEntityEvent(byte data) {
    if (data == (byte)10) {
      toggleCartRunning();
    }
  }

  public void toggleCartRunning () {
    setCartRunning(!powered);
  }

  protected void setCartRunning(boolean power) {
    powered = power;
    toggleCooldown = COOLDOWN;
    if (!level.isClientSide()) {
    //  Railways.LOGGER.error("toggled cart state");
      level.broadcastEntityEvent(this, (byte)10);
    }
  }

  public BlockState getDisplayBlockState () {
    boolean negative = getMotionDirection().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
    return content.setValue(SteamCartBlock.POWERED, powered).setValue(SteamCartBlock.FACING, negative ? Direction.WEST : Direction.EAST);
  }

  protected void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.putDouble("PushX", this.xPush);
    tag.putDouble("PushZ", this.zPush);
    tag.putInt("Fuel", this.fuel);
    tag.putInt("Water", this.water);
    tag.putBoolean("Powered", this.powered);
  }

  protected void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    this.xPush   = tag.getDouble("PushX");
    this.zPush   = tag.getDouble("PushZ");
    this.fuel    = tag.getInt("Fuel");
    this.water   = tag.getInt("Water");
    this.powered = tag.getBoolean("Powered");

    // a "kick" to start it when the world reloads
    double vsq = xPush * xPush + zPush * zPush;
    if (powered && vsq > 0.1E-4d) {
      setDeltaMovement(xPush, 0d, zPush);
    }
  }
}
