package com.railwayteam.railways.entities.handcar;

import com.railwayteam.railways.entities.TrackRidingEntity;
import com.railwayteam.railways.items.ConductorItem;
import com.railwayteam.railways.items.handcar.HandcarItem;
import com.railwayteam.railways.util.Animatable;
import com.railwayteam.railways.util.WrenchableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.List;

public class HandcarEntity extends TrackRidingEntity implements WrenchableEntity {
    public static final String name = "handcar";

    boolean pushDirection = true; // used in hand car animation so it switches directions at some point

    // TODO: when the actual track riding is done, please add these values
    public boolean shouldMoveWheels() {
        return true;
    }

    public boolean shouldPushWalkingBeam() {
        return true;
    }

    // TODO: change these 2 negative when moving in the other direction
    // TODO: make this change depending on the movement speed
    public double getRotateWheelsBy() {
        return 0.001;
    }

    public double getPushWalkingBeamBy() {
        return 0.01;
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

    @Override
    protected boolean canFitPassenger(Entity p_184219_1_) {
        return this.getPassengers().size() < 2;
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
}
