package com.railwayteam.railways.entities.handcar;

import com.railwayteam.railways.entities.TrackRidingEntity;
import com.railwayteam.railways.util.Animatable;
import com.railwayteam.railways.util.WrenchableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;

public class HandcarEntity extends TrackRidingEntity implements Animatable, WrenchableEntity {
    public static final String name = "handcar";
    public boolean isMoving = true; // TODO: when the actual track riding is done, please set this to whether or not it is moving
    // there are only 2 ways the wheels can move, so i guess a boolean is fine
    public boolean movementDirection = true; // TODO: when the track riding is done, please set this to the direction

    public double wheelZ = 0;

    public HandcarEntity(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @Override
    public double getMaxSpeed() {
        return 0.2;
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
    public HandSide getPrimaryHand() { return null; }

    private AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity plr, Vector3d pos, Hand hand) {
        return onWrenched(plr, hand, this);
//        return super.applyPlayerInteraction(p_184199_1_, p_184199_2_, p_184199_3_);
    }

    @Override
    public <E extends IAnimatable> AnimationBuilder getAnimation(AnimationEvent<E> event) {
        return anim("push");
    }

    @Override
    public <E extends IAnimatable> PlayState getPlayState(AnimationEvent<E> event, AnimationBuilder returnedAnimation) {
        return isMoving ? PlayState.CONTINUE : PlayState.STOP;
    }
}
