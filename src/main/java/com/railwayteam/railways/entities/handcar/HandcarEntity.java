package com.railwayteam.railways.entities.handcar;

import com.railwayteam.railways.entities.TrackRidingEntity;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
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

public class HandcarEntity extends TrackRidingEntity implements Animatable, WrenchableEntity {
    public static final String name = "handcar";
    public boolean isMoving = true; // TODO: when the actual track riding is done, please set this to whether or not it is moving
    // there are only 2 ways the wheels can move, so i guess a boolean is fine
    public boolean movementDirection = true; // TODO: when the track riding is done, please set this to the direction

    public double wheelZ = 0;
    public float deltaRotation;

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
    protected boolean canFitPassenger(Entity p_184219_1_) {
        return this.getPassengers().size() < 2;
    }

    public ActionResultType processInitialInteract(PlayerEntity plr, Hand hand) {
        if (!this.world.isRemote) {
            return plr.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public double getMountedYOffset() {
        return 1;
    }

    @Override
    public void updatePassenger(Entity entity) { // copy paste go brrrrrr
        if (this.isPassenger(entity)) {
            float f = 0.7F;
            float f1 = (float)((this.isAlive() ? (double)0.01F : this.getMountedYOffset()) + entity.getYOffset());
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(entity);
                if (i == 0) {
                    f = 0.3F;
                } else {
                    f = -0.6F;
                }
            }

            Vector3d vector3d = (new Vector3d(f, 0.0D, 0.0D)).rotateYaw(-this.rotationYawHead * ((float)Math.PI / 180F) - ((float)Math.PI / 2F));
            entity.setPosition(this.getX() + vector3d.x, this.getY() + (double)f1, this.getZ() + vector3d.z);
//            entity.rotationYaw += this.deltaRotation;
//            entity.setRotationYawHead(entity.getRotationYawHead() + this.deltaRotation);
//            this.applyYawToEntity(entity);
            if (entity instanceof AnimalEntity && this.getPassengers().size() > 1) {
                int j = entity.getEntityId() % 2 == 0 ? 90 : 270;
                entity.setRenderYawOffset(((AnimalEntity)entity).renderYawOffset + (float)j);
                entity.setRotationYawHead(entity.getRotationYawHead() + (float)j);
            }

        }
    }

    @Override
    public void tick() {
        super.tick();
//        System.out.println(this.rotationYaw + " " + world.isRemote);
    }

    //    public void applyYawToEntity(Entity p_184454_1_) {
//        p_184454_1_.setRenderYawOffset(this.rotationYaw);
//        float f = MathHelper.wrapDegrees(p_184454_1_.rotationYaw - this.rotationYaw);
//        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
//        p_184454_1_.prevRotationYaw += f1 - f;
//        p_184454_1_.rotationYaw += f1 - f;
//        p_184454_1_.setRotationYawHead(p_184454_1_.rotationYaw);
//    }

    @Override
    public <E extends IAnimatable> AnimationBuilder getAnimation(AnimationEvent<E> event) {
        return anim("push");
    }

    @Override
    public <E extends IAnimatable> PlayState getPlayState(AnimationEvent<E> event, AnimationBuilder returnedAnimation) {
        return isMoving ? PlayState.CONTINUE : PlayState.STOP;
    }

    @Override
    public void afterWrenched(PlayerEntity plr, Hand hand) {
        entityDropItem(HandcarItem.g().create(this));
    }
}
