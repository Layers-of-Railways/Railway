package com.railwayteam.railways.content.entities.handcar;

import com.railwayteam.railways.content.entities.TrackRidingEntity;
import com.railwayteam.railways.content.items.HandcarItem;
import com.railwayteam.railways.util.WrenchableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class HandcarEntity extends TrackRidingEntity implements WrenchableEntity {
    public static final String name = "handcar";

    boolean pushDirection = true; // used in hand car animation so it switches directions at some point

    float wheelRotationZ = 0;
    float walkingBeamRotationX = 0;

    // TODO: change these 2 negative when moving in the other direction, and to zero when shouldnt move
    // TODO: make this change depending on the movement speed
    public double getRotateWheelsBy() {
        return 0.01;
    }

    public double getPushWalkingBeamBy() {
        return 0.01;
    }

    @Override
    public void tick() {
        if(world.isRemote) {
            if(walkingBeamRotationX >= 0.4) {
                pushDirection = false;
            } else if(walkingBeamRotationX <= -0.4) {
                pushDirection = true;
            }
            walkingBeamRotationX += getPushWalkingBeamBy() * (pushDirection ? 1D : -1D);

            wheelRotationZ += getRotateWheelsBy();
            wheelRotationZ %= Math.PI * 2;
        }

        super.tick();
    }

    public HandcarEntity(EntityType<? extends Entity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @Override
    public void spawnDrops(DamageSource p_213345_1_) {
        entityDropItem(HandcarItem.g().create(this));
    }

    @Override
    public double getRidingOffset() {
        return 1;
    }

    public Iterable<ItemStack> getArmorInventoryList() {
        return new ArrayList<>();
    }

    public ItemStack getItemStackFromSlot(EquipmentSlotType slotType) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotType, ItemStack stack) {
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity plr, Vector3d pos, Hand hand) {
        return onWrenched(plr, hand, this);
//        return super.applyPlayerInteraction(p_184199_1_, p_184199_2_, p_184199_3_);
    }

    //@Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    protected boolean canFitPassenger(Entity p_184219_1_) {
        return this.getPassengers().size() < 2;
    }

    @Override
    public boolean canBeCollidedWith() {
        return isAlive();
    }

    @Override
    public double getPushBoxX() {
        return 0.3;
    }

    @Override
    public double getPushBoxY() {
        return 0.3;
    }

    @Override
    public double getPushBoxZ() {
        return 0.3;
    }

    public ActionResultType processInitialInteract(PlayerEntity plr, Hand hand) {
        if(plr.isSneaking()) {
            return onWrenched(plr, hand, this);
        }
        if (!this.world.isRemote) {
            return plr.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public void afterWrenched(PlayerEntity plr, Hand hand) {
        entityDropItem(HandcarItem.g().create(this));
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return HandcarItem.g().create(this);
    }
}
