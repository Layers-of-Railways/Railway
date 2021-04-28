package com.railwayteam.railways.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SteadyMinecartEntity extends MinecartEntity {
  public static final String name = "steadycart";

  private double lockedSpeed = 0.2d;
  private double storedSpeed = 0d;

  public SteadyMinecartEntity (EntityType<?> p_i50126_1_, World p_i50126_2_) {
    super(p_i50126_1_, p_i50126_2_);
  }

  public SteadyMinecartEntity (World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  @Override
  public boolean isPoweredCart() {
    return (storedSpeed > 0.01d);
  }

  @Override
  public void moveTo(Vector3d motionIn) {
    double mag = getHorizontalDistanceSqr(motionIn);
    if ((Math.abs(motionIn.x()) > lockedSpeed) || (Math.abs(motionIn.z()) > lockedSpeed)) {
      motionIn = motionIn.multiply(lockedSpeed/mag, 1, lockedSpeed/mag);
    }
    super.moveTo(motionIn);
  }

  @Override
  public void moveTo(double x, double y, double z) {
    this.moveTo(new Vector3d(x,y,z));
  }

  //@Override
  //protected void applyDrag() {
    //storedSpeed -= 0.001d;
    //if (storedSpeed < 0.01d) {
    //  storedSpeed = 0d;
    //  super.applyDrag();
    //}
  //}

  @Override
  public ActionResultType interact(PlayerEntity player, Hand hand) {
    return super.interact(player, hand);
  }

  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
