package com.railwayteam.railways.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

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

    private static final DataParameter<Integer> HEALTH = EntityDataManager.createKey(TrackRidingEntity.class, DataSerializers.VARINT);

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
                spawnDrops(damageSource);
                this.remove();
            }

            return true;
        } else {
            return true;
        }
    }

    protected float getWaterSlowDown() {
        return 0.8F;
    }

    public Vector3d unknownMovementMethod1(double p_233626_1_, boolean p_233626_3_, Vector3d p_233626_4_) {
        if (!this.hasNoGravity() && !this.isSprinting()) {
            double d0;
            if (p_233626_3_ && Math.abs(p_233626_4_.y - 0.005D) >= 0.003D && Math.abs(p_233626_4_.y - p_233626_1_ / 16.0D) < 0.003D) {
                d0 = -0.003D;
            } else {
                d0 = p_233626_4_.y - p_233626_1_ / 16.0D;
            }

            return new Vector3d(p_233626_4_.x, d0, p_233626_4_.z);
        } else {
            return p_233626_4_;
        }
    }

    public double getGravity() {
        return 0.08D;
    }

    public double getFallBy() {
        return getGravity() / -2; // -0.04
    }

    public Vector3d unknownMovementMethod2(Vector3d p_233633_1_, float p_233633_2_) {
        this.move(MoverType.SELF, this.getMotion());
        Vector3d vector3d = this.getMotion();
        if (this.collidedHorizontally) {
            vector3d = new Vector3d(vector3d.x, 0.2D, vector3d.z);
        }

        return vector3d;
    }

    @Override
    public void tick() {
        super.tick();

        move();
        pushEntities();
        scaleMotion();
    }

    public void scaleMotion() {
        setMotion(this.getMotion().mul(getScaleDownMotionBy(), 1, getScaleDownMotionBy()));
    }

    public double getScaleDownMotionBy() {
        return 0.5D;
    }

    public abstract double getPushBoxX();
    public abstract double getPushBoxY();
    public abstract double getPushBoxZ();

    public AxisAlignedBB getPushBox() {
        return getBoundingBox().grow(getPushBoxX(), getPushBoxY(), getPushBoxZ());
    }

    public void pushEntities() {
        this.world.getEntitiesInAABBexcluding(this, getPushBox(), EntityPredicates.pushableBy(this))
            .forEach(entity -> entity.applyEntityCollision(this));
    }

    public void move() {
        double gravity = getGravity();
        boolean isNotFalling = this.getMotion().y <= 0.0D;

        if (this.isInWater()) {
            waterMove(gravity, isNotFalling);
        } else {
            gravityMove();
        }

        this.move(MoverType.SELF, this.getMotion());
    }

    public void gravityMove() {
        this.setMotion(this.getMotion().add(0.0D, getFallBy(), 0.0D));
    }

    public void waterMove(double gravity, boolean isNotFalling) {
        double posY = this.getY();
        float moveBy = this.isSprinting() ? 0.9F : this.getWaterSlowDown();

//            this.moveRelative(f6, new Vector3d((double)this.moveStrafing, (double)this.moveVertical, (double)this.moveForward));
        Vector3d vector3d6 = this.getMotion();

        this.setMotion(vector3d6.mul(moveBy, 0.8F, moveBy));
        Vector3d vector3d2 = this.unknownMovementMethod1(gravity, isNotFalling, this.getMotion());
        this.setMotion(vector3d2);
        if (this.collidedHorizontally && this.isOffsetPositionInLiquid(vector3d2.x, vector3d2.y + (double)0.6F - this.getY() + posY, vector3d2.z)) {
            this.setMotion(vector3d2.x, 0.3F, vector3d2.z);
        }
    }
}
