package com.railwayteam.railways.entities;

import com.mojang.datafixers.util.Pair;
import com.railwayteam.railways.blocks.AbstractLargeTrackBlock;
import com.railwayteam.railways.entities.handcar.HandcarEntity;
import com.railwayteam.railways.items.handcar.HandcarItem;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// TODO: literally the whole class
public abstract class TrackRidingEntity extends Entity {
    public TrackRidingEntity(EntityType<? extends Entity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @Override
    public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

    @Override
    protected void readAdditional(CompoundNBT p_70037_1_) {

    }

    @Override
    protected void writeAdditional(CompoundNBT p_213281_1_) {

    }

    public abstract void spawnDrops(DamageSource p_213345_1_);

    private static final DataParameter<Integer> HEALTH = EntityDataManager.createKey(HandcarEntity.class, DataSerializers.VARINT);

    @Override
    protected void registerData() {
        this.dataManager.register(HEALTH, getMaxHealth());
    }

    public void setHealth(int value) {
        dataManager.set(HEALTH, value);
    }

    public int getHealth() {
        return dataManager.get(HEALTH);
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public int getMaxHealth() {
        return 20;
    }

    public abstract double getRidingOffset();

    @Override
    public double getMountedYOffset() {
        return getRidingOffset();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        } else if (!this.world.isRemote && !this.isDead()) {
//            this.setForwardDirection(-this.getForwardDirection());
//            this.setTimeSinceHit(10);
//            this.setDamageTaken(this.getDamageTaken() + p_70097_2_ * 10.0F);
            this.markVelocityChanged();
            setHealth(getHealth() - (int) amount);
            boolean flag = damageSource.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)damageSource.getTrueSource()).abilities.isCreativeMode;
            if (flag || isDead()) {
                if (!flag && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    spawnDrops(damageSource);
                }

                this.remove();
            }

            return true;
        } else {
            return true;
        }
    }
}
