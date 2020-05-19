package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class BeehiveTileEntity extends TileEntity implements ITickableTileEntity {
   private final List<BeehiveTileEntity.Bee> bees = Lists.newArrayList();
   @Nullable
   private BlockPos flowerPos = null;

   public BeehiveTileEntity() {
      super(TileEntityType.BEEHIVE);
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty() {
      if (this.isNearFire()) {
         this.angerBees((PlayerEntity)null, this.world.getBlockState(this.getPos()), BeehiveTileEntity.State.EMERGENCY);
      }

      super.markDirty();
   }

   public boolean isNearFire() {
      if (this.world == null) {
         return false;
      } else {
         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
            if (this.world.getBlockState(blockpos).getBlock() instanceof FireBlock) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean hasNoBees() {
      return this.bees.isEmpty();
   }

   public boolean isFullOfBees() {
      return this.bees.size() == 3;
   }

   public void angerBees(@Nullable PlayerEntity p_226963_1_, BlockState p_226963_2_, BeehiveTileEntity.State p_226963_3_) {
      List<Entity> list = this.tryReleaseBee(p_226963_2_, p_226963_3_);
      if (p_226963_1_ != null) {
         for(Entity entity : list) {
            if (entity instanceof BeeEntity) {
               BeeEntity beeentity = (BeeEntity)entity;
               if (p_226963_1_.getPositionVec().squareDistanceTo(entity.getPositionVec()) <= 16.0D) {
                  if (!this.isSmoked()) {
                     beeentity.setBeeAttacker(p_226963_1_);
                  } else {
                     beeentity.setStayOutOfHiveCountdown(400);
                  }
               }
            }
         }
      }

   }

   private List<Entity> tryReleaseBee(BlockState p_226965_1_, BeehiveTileEntity.State p_226965_2_) {
      List<Entity> list = Lists.newArrayList();
      this.bees.removeIf((p_226966_4_) -> {
         return this.releaseBee(p_226965_1_, p_226966_4_.entityData, list, p_226965_2_);
      });
      return list;
   }

   public void tryEnterHive(Entity p_226961_1_, boolean p_226961_2_) {
      this.tryEnterHive(p_226961_1_, p_226961_2_, 0);
   }

   public int getBeeCount() {
      return this.bees.size();
   }

   public static int getHoneyLevel(BlockState p_226964_0_) {
      return p_226964_0_.get(BeehiveBlock.HONEY_LEVEL);
   }

   public boolean isSmoked() {
      return CampfireBlock.isLitCampfireInRange(this.world, this.getPos(), 5);
   }

   protected void sendDebugData() {
      DebugPacketSender.sendBeehiveDebugData(this);
   }

   public void tryEnterHive(Entity p_226962_1_, boolean p_226962_2_, int p_226962_3_) {
      if (this.bees.size() < 3) {
         p_226962_1_.stopRiding();
         p_226962_1_.removePassengers();
         CompoundNBT compoundnbt = new CompoundNBT();
         p_226962_1_.writeUnlessPassenger(compoundnbt);
         this.bees.add(new BeehiveTileEntity.Bee(compoundnbt, p_226962_3_, p_226962_2_ ? 2400 : 600));
         if (this.world != null) {
            if (p_226962_1_ instanceof BeeEntity) {
               BeeEntity beeentity = (BeeEntity)p_226962_1_;
               if (beeentity.hasFlower() && (!this.hasFlowerPos() || this.world.rand.nextBoolean())) {
                  this.flowerPos = beeentity.getFlowerPos();
               }
            }

            BlockPos blockpos = this.getPos();
            this.world.playSound((PlayerEntity)null, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         p_226962_1_.remove();
      }
   }

   private boolean releaseBee(BlockState p_226967_1_, CompoundNBT p_226967_2_, @Nullable List<Entity> p_226967_3_, BeehiveTileEntity.State p_226967_4_) {
      BlockPos blockpos = this.getPos();
      if ((this.world.isNightTime() || this.world.isRaining()) && p_226967_4_ != BeehiveTileEntity.State.EMERGENCY) {
         return false;
      } else {
         p_226967_2_.remove("Passengers");
         p_226967_2_.remove("Leash");
         p_226967_2_.removeUniqueId("UUID");
         Direction direction = p_226967_1_.get(BeehiveBlock.FACING);
         BlockPos blockpos1 = blockpos.offset(direction);
         boolean flag = !this.world.getBlockState(blockpos1).getCollisionShape(this.world, blockpos1).isEmpty();
         if (flag && p_226967_4_ != BeehiveTileEntity.State.EMERGENCY) {
            return false;
         } else {
            Entity entity = EntityType.func_220335_a(p_226967_2_, this.world, (p_226960_0_) -> {
               return p_226960_0_;
            });
            if (entity != null) {
               float f = entity.getWidth();
               double d0 = flag ? 0.0D : 0.55D + (double)(f / 2.0F);
               double d1 = (double)blockpos.getX() + 0.5D + d0 * (double)direction.getXOffset();
               double d2 = (double)blockpos.getY() + 0.5D - (double)(entity.getHeight() / 2.0F);
               double d3 = (double)blockpos.getZ() + 0.5D + d0 * (double)direction.getZOffset();
               entity.setLocationAndAngles(d1, d2, d3, entity.rotationYaw, entity.rotationPitch);
               if (!entity.getType().isContained(EntityTypeTags.BEEHIVE_INHABITORS)) {
                  return false;
               } else {
                  if (entity instanceof BeeEntity) {
                     BeeEntity beeentity = (BeeEntity)entity;
                     if (this.hasFlowerPos() && !beeentity.hasFlower() && this.world.rand.nextFloat() < 0.9F) {
                        beeentity.setFlowerPos(this.flowerPos);
                     }

                     if (p_226967_4_ == BeehiveTileEntity.State.HONEY_DELIVERED) {
                        beeentity.onHoneyDelivered();
                        if (p_226967_1_.getBlock().isIn(BlockTags.BEEHIVES)) {
                           int i = getHoneyLevel(p_226967_1_);
                           if (i < 5) {
                              int j = this.world.rand.nextInt(100) == 0 ? 2 : 1;
                              if (i + j > 5) {
                                 --j;
                              }

                              this.world.setBlockState(this.getPos(), p_226967_1_.with(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
                           }
                        }
                     }

                     beeentity.resetTicksWithoutNectar();
                     if (p_226967_3_ != null) {
                        p_226967_3_.add(beeentity);
                     }
                  }

                  BlockPos blockpos2 = this.getPos();
                  this.world.playSound((PlayerEntity)null, (double)blockpos2.getX(), (double)blockpos2.getY(), (double)blockpos2.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  return this.world.addEntity(entity);
               }
            } else {
               return false;
            }
         }
      }
   }

   private boolean hasFlowerPos() {
      return this.flowerPos != null;
   }

   private void tickBees() {
      Iterator<BeehiveTileEntity.Bee> iterator = this.bees.iterator();
      BlockState blockstate = this.getBlockState();

      while(iterator.hasNext()) {
         BeehiveTileEntity.Bee beehivetileentity$bee = iterator.next();
         if (beehivetileentity$bee.ticksInHive > beehivetileentity$bee.minOccupationTicks) {
            CompoundNBT compoundnbt = beehivetileentity$bee.entityData;
            BeehiveTileEntity.State beehivetileentity$state = compoundnbt.getBoolean("HasNectar") ? BeehiveTileEntity.State.HONEY_DELIVERED : BeehiveTileEntity.State.BEE_RELEASED;
            if (this.releaseBee(blockstate, compoundnbt, (List<Entity>)null, beehivetileentity$state)) {
               iterator.remove();
            }
         } else {
            beehivetileentity$bee.ticksInHive++;
         }
      }

   }

   public void tick() {
      if (!this.world.isRemote) {
         this.tickBees();
         BlockPos blockpos = this.getPos();
         if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005D) {
            double d0 = (double)blockpos.getX() + 0.5D;
            double d1 = (double)blockpos.getY();
            double d2 = (double)blockpos.getZ() + 0.5D;
            this.world.playSound((PlayerEntity)null, d0, d1, d2, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         this.sendDebugData();
      }
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.bees.clear();
      ListNBT listnbt = compound.getList("Bees", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         BeehiveTileEntity.Bee beehivetileentity$bee = new BeehiveTileEntity.Bee(compoundnbt.getCompound("EntityData"), compoundnbt.getInt("TicksInHive"), compoundnbt.getInt("MinOccupationTicks"));
         this.bees.add(beehivetileentity$bee);
      }

      this.flowerPos = null;
      if (compound.contains("FlowerPos")) {
         this.flowerPos = NBTUtil.readBlockPos(compound.getCompound("FlowerPos"));
      }

   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      compound.put("Bees", this.getBees());
      if (this.hasFlowerPos()) {
         compound.put("FlowerPos", NBTUtil.writeBlockPos(this.flowerPos));
      }

      return compound;
   }

   public ListNBT getBees() {
      ListNBT listnbt = new ListNBT();

      for(BeehiveTileEntity.Bee beehivetileentity$bee : this.bees) {
         beehivetileentity$bee.entityData.removeUniqueId("UUID");
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.put("EntityData", beehivetileentity$bee.entityData);
         compoundnbt.putInt("TicksInHive", beehivetileentity$bee.ticksInHive);
         compoundnbt.putInt("MinOccupationTicks", beehivetileentity$bee.minOccupationTicks);
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   static class Bee {
      private final CompoundNBT entityData;
      private int ticksInHive;
      private final int minOccupationTicks;

      private Bee(CompoundNBT p_i225767_1_, int p_i225767_2_, int p_i225767_3_) {
         p_i225767_1_.removeUniqueId("UUID");
         this.entityData = p_i225767_1_;
         this.ticksInHive = p_i225767_2_;
         this.minOccupationTicks = p_i225767_3_;
      }
   }

   public static enum State {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY;
   }
}