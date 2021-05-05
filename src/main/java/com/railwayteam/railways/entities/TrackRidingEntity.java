package com.railwayteam.railways.entities;

import com.mojang.datafixers.util.Pair;
import com.railwayteam.railways.blocks.AbstractLargeTrackBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// gotta use LivingEntity for an animatable entity, so i guess.. :/
public abstract class TrackRidingEntity extends LivingEntity {
    public TrackRidingEntity(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @Override
    public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

    protected void moveAlongTrack(BlockPos pos, BlockState state) {
        this.fallDistance = 0.0F;
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        Vector3d p = this.movePos(d0, d1, d2);
        d1 = pos.getY();
        boolean flag = false;
        boolean flag1 = false;
        AbstractLargeTrackBlock track = (AbstractLargeTrackBlock) state.getBlock();

        double d3 = 0.0078125D;
        Vector3d vector3d1 = this.getMotion();
        ArrayList<BlockPos> pair = track.getConnections(world, pos);
        BlockPos vector3i = pos;
        BlockPos vector3i1 = pos;
        if(!pair.isEmpty()) {
            vector3i = pair.get(0);
        }
        if(pair.size() > 1) {
            vector3i1 = pair.get(1);
        }
        double d4 = vector3i1.getX() - vector3i.getX();
        double d5 = vector3i1.getZ() - vector3i.getZ();
        double d6 = Math.sqrt(d4 * d4 + d5 * d5);
        double d7 = vector3d1.x * d4 + vector3d1.z * d5;
        if (d7 < 0.0D) {
            d4 = -d4;
            d5 = -d5;
        }

        double d8 = Math.min(2.0D, Math.sqrt(horizontalMag(vector3d1)));
        vector3d1 = new Vector3d(d8 * d4 / d6, vector3d1.y, d8 * d5 / d6);
        this.setMotion(vector3d1);
        Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
        if (entity instanceof PlayerEntity) {
            Vector3d vector3d2 = entity.getMotion();
            double d9 = horizontalMag(vector3d2);
            double d11 = horizontalMag(this.getMotion());
            if (d9 > 1.0E-4D && d11 < 0.01D) {
                this.setMotion(this.getMotion().add(vector3d2.x * 0.1D, 0.0D, vector3d2.z * 0.1D));
                flag1 = false;
            }
        }

        double d23 = (double)pos.getX() + 0.5D + (double)vector3i.getX() * 0.5D;
        double d10 = (double)pos.getZ() + 0.5D + (double)vector3i.getZ() * 0.5D;
        double d12 = (double)pos.getX() + 0.5D + (double)vector3i1.getX() * 0.5D;
        double d13 = (double)pos.getZ() + 0.5D + (double)vector3i1.getZ() * 0.5D;
        d4 = d12 - d23;
        d5 = d13 - d10;
        double d14;
        if (d4 == 0.0D) {
            d14 = d2 - (double)pos.getZ();
        } else if (d5 == 0.0D) {
            d14 = d0 - (double)pos.getX();
        } else {
            double d15 = d0 - d23;
            double d16 = d2 - d10;
            d14 = (d15 * d4 + d16 * d5) * 2.0D;
        }

        d0 = d23 + d4 * d14;
        d2 = d10 + d5 * d14;
        this.setPosition(d0, d1, d2);
        this.move(pos);
        if (vector3i.getY() != 0 && MathHelper.floor(this.getX()) - pos.getX() == vector3i.getX() && MathHelper.floor(this.getZ()) - pos.getZ() == vector3i.getZ()) {
            this.setPosition(this.getX(), this.getY() + (double)vector3i.getY(), this.getZ());
        } else if (vector3i1.getY() != 0 && MathHelper.floor(this.getX()) - pos.getX() == vector3i1.getX() && MathHelper.floor(this.getZ()) - pos.getZ() == vector3i1.getZ()) {
            this.setPosition(this.getX(), this.getY() + (double)vector3i1.getY(), this.getZ());
        }

        this.applyDrag();
        Vector3d vector3d3 = this.movePos(this.getX(), this.getY(), this.getZ());
        if (vector3d3 != null && p != null) {
            double d17 = (p.y - vector3d3.y) * 0.05D;
            Vector3d vector3d4 = this.getMotion();
            double d18 = Math.sqrt(horizontalMag(vector3d4));
            if (d18 > 0.0D) {
                this.setMotion(vector3d4.mul((d18 + d17) / d18, 1.0D, (d18 + d17) / d18));
            }

            this.setPosition(this.getX(), vector3d3.y, this.getZ());
        }

        int j = MathHelper.floor(this.getX());
        int i = MathHelper.floor(this.getZ());
        if (j != pos.getX() || i != pos.getZ()) {
            Vector3d vector3d5 = this.getMotion();
            double d26 = Math.sqrt(horizontalMag(vector3d5));
            this.setMotion(d26 * (double)(j - pos.getX()), vector3d5.y, d26 * (double)(i - pos.getZ()));
        }

//        if (shouldDoRailFunctions())
//            ((AbstractRailBlock)p_180460_2_.getBlock()).onMinecartPass(p_180460_2_, world, p_180460_1_, this);
//        }
    }

    protected boolean isInReverse;

    public boolean isInReverse() {return isInReverse;}

    @Override
    public void tick() {
        super.tick();

        // TODO: A lot of this stuff is TODO, just trying to get it working first before adding these details
        if (this.getY() < -64.0D) {
            this.outOfWorld();
        }

        this.updatePortal();
        if (this.world.isRemote) {
//            if (this.turnProgress > 0) {
//                double d4 = this.getX() + (this.minecartX - this.getX()) / (double)this.turnProgress;
//                double d5 = this.getY() + (this.minecartY - this.getY()) / (double)this.turnProgress;
//                double d6 = this.getZ() + (this.minecartZ - this.getZ()) / (double)this.turnProgress;
//                double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
//                this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
//                this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
//                --this.turnProgress;
//                this.setPosition(d4, d5, d6);
//                this.setRotation(this.rotationYaw, this.rotationPitch);
//            } else {
//                this.refreshPosition();
//                this.setRotation(this.rotationYaw, this.rotationPitch);
//            }
            // TODO: add this rotation stuff
        } else {
            if (!this.hasNoGravity()) {
                this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
            }

            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY());
            int k = MathHelper.floor(this.getZ());
            if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
                --j;
            }

            BlockPos blockpos = new BlockPos(i, j, k);
            BlockState blockstate = this.world.getBlockState(blockpos);
            if (AbstractLargeTrackBlock.isTrack(blockstate)) {
                this.moveAlongTrack(blockpos, blockstate);
//                if (blockstate.getBlock() instanceof PoweredRailBlock && ((PoweredRailBlock) blockstate.getBlock()).isActivatorRail()) {
//                    this.onActivatorRailPass(i, j, k, blockstate.get(PoweredRailBlock.POWERED));
//                }
            } else {
//                this.moveDerailedMinecart();
                // TODO: derailed movement
            }

            this.doBlockCollisions();
            this.rotationPitch = 0.0F;
            double d0 = this.prevPosX - this.getX();
            double d2 = this.prevPosZ - this.getZ();
            if (d0 * d0 + d2 * d2 > 0.001D) {
                this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI);
                if (this.isInReverse) {
                    this.rotationYaw += 180.0F;
                }
            }

            double d3 = MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);
            if (d3 < -170.0D || d3 >= 170.0D) {
                this.rotationYaw += 180.0F;
                this.isInReverse = !this.isInReverse;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);
            // TODO: this collision stuff or whatever
//            AxisAlignedBB box;
//            if (getCollisionHandler() != null) box = getCollisionHandler().getMinecartCollisionBox(this);
//            else                               box = this.getBoundingBox().grow(0.2F, 0.0D, 0.2F);
//            if (canBeRidden() && horizontalMag(this.getMotion()) > 0.01D) {
//                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, box, EntityPredicates.pushableBy(this));
//                if (!list.isEmpty()) {
//                    for(int l = 0; l < list.size(); ++l) {
//                        Entity entity1 = list.get(l);
//                        if (!(entity1 instanceof PlayerEntity) && !(entity1 instanceof IronGolemEntity) && !(entity1 instanceof AbstractMinecartEntity) && !this.isBeingRidden() && !entity1.isPassenger()) {
//                            entity1.startRiding(this);
//                        } else {
//                            entity1.applyEntityCollision(this);
//                        }
//                    }
//                }
//            } else {
//                for(Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, box)) {
//                    if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof AbstractMinecartEntity) {
//                        entity.applyEntityCollision(this);
//                    }
//                }
//            }

            this.updateWaterState();
            if (this.isInLava()) {
                this.setOnFireFromLava();
                this.fallDistance *= 0.5F;
            }

            this.firstUpdate = false;
        }
    }

    public void applyDrag() {
        double d0 = this.isBeingRidden() ? 0.997D : 0.96D;
        this.setMotion(this.getMotion().mul(d0, 0.0D, d0));
    }

    public abstract double getMaxSpeed();

    public void move(BlockPos pos) { //Non-default because getMaximumSpeed is protected
        double d24 = isBeingRidden() ? 0.75D : 1.0D;
        double d25 = getMaxSpeed();
        Vector3d vec3d1 = getMotion();
        move(MoverType.SELF, new Vector3d(MathHelper.clamp(d24 * vec3d1.x, -d25, d25), 0.0D, MathHelper.clamp(d24 * vec3d1.z, -d25, d25)));
    }

    protected Vector3d movePos(double x, double y, double z) {
            int i = MathHelper.floor(x);
            int j = MathHelper.floor(y);
            int k = MathHelper.floor(z);
            if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
                --j;
            }

             BlockPos trackPos = new BlockPos(i, j, k);
            BlockState blockstate = this.world.getBlockState(trackPos);
            if (AbstractLargeTrackBlock.isTrack(blockstate)) {
                AbstractLargeTrackBlock track = (AbstractLargeTrackBlock) blockstate.getBlock();
//                RailShape railshape = ((AbstractLargeTrackBlock)blockstate.getBlock()).(blockstate, this.world, new BlockPos(i, j, k), this);
                ArrayList<BlockPos> posList = track.getConnections(world, trackPos);
                System.out.println(posList);
                BlockPos vector3i = trackPos;
                BlockPos vector3i1 = trackPos;
//                if(posList.size() > 1) {
//                    vector3i = posList.get(0);
//                    vector3i1 = posList.get(1);
//                };

                if(!posList.isEmpty()) {
                    vector3i = posList.get(0);
                }
                if(posList.size() > 1) {
                    vector3i1 = posList.get(1);
                }

                double d0 = (double)i + 0.5D + (double)vector3i.getX() * 0.5D;
                double d1 = (double)j + 0.0625D + (double)vector3i.getY() * 0.5D;
                double d2 = (double)k + 0.5D + (double)vector3i.getZ() * 0.5D;
                double d3 = (double)i + 0.5D + (double)vector3i1.getX() * 0.5D;
                double d4 = (double)j + 0.0625D + (double)vector3i1.getY() * 0.5D;
                double d5 = (double)k + 0.5D + (double)vector3i1.getZ() * 0.5D;
                double d6 = d3 - d0;
                double d7 = (d4 - d1) * 2.0D;
                double d8 = d5 - d2;
                double d9;
                if (d6 == 0.0D) {
                    d9 = z - (double)k;
                } else if (d8 == 0.0D) {
                    d9 = x - (double)i;
                } else {
                    double d10 = x - d0;
                    double d11 = z - d2;
                    d9 = (d10 * d6 + d11 * d8) * 2.0D;
                }

                x = d0 + d6 * d9;
                y = d1 + d7 * d9;
                z = d2 + d8 * d9;
                if (d7 < 0.0D) {
                    ++y;
                } else if (d7 > 0.0D) {
                    y += 0.5D;
                }

                return new Vector3d(x, y, z);
            } else {
                return null;
            }
        }
}
