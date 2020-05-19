package net.minecraft.entity.passive.fish;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowSchoolLeaderGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractGroupFishEntity extends AbstractFishEntity {
   private AbstractGroupFishEntity groupLeader;
   private int groupSize = 1;

   public AbstractGroupFishEntity(EntityType<? extends AbstractGroupFishEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(5, new FollowSchoolLeaderGoal(this));
   }

   /**
    * Will return how many at most can spawn in a chunk at once.
    */
   public int getMaxSpawnedInChunk() {
      return this.getMaxGroupSize();
   }

   public int getMaxGroupSize() {
      return super.getMaxSpawnedInChunk();
   }

   protected boolean func_212800_dy() {
      return !this.hasGroupLeader();
   }

   public boolean hasGroupLeader() {
      return this.groupLeader != null && this.groupLeader.isAlive();
   }

   public AbstractGroupFishEntity func_212803_a(AbstractGroupFishEntity groupLeaderIn) {
      this.groupLeader = groupLeaderIn;
      groupLeaderIn.increaseGroupSize();
      return groupLeaderIn;
   }

   public void leaveGroup() {
      this.groupLeader.decreaseGroupSize();
      this.groupLeader = null;
   }

   private void increaseGroupSize() {
      ++this.groupSize;
   }

   private void decreaseGroupSize() {
      --this.groupSize;
   }

   public boolean canGroupGrow() {
      return this.isGroupLeader() && this.groupSize < this.getMaxGroupSize();
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.isGroupLeader() && this.world.rand.nextInt(200) == 1) {
         List<AbstractFishEntity> list = this.world.getEntitiesWithinAABB(this.getClass(), this.getBoundingBox().grow(8.0D, 8.0D, 8.0D));
         if (list.size() <= 1) {
            this.groupSize = 1;
         }
      }

   }

   public boolean isGroupLeader() {
      return this.groupSize > 1;
   }

   public boolean inRangeOfGroupLeader() {
      return this.getDistanceSq(this.groupLeader) <= 121.0D;
   }

   public void moveToGroupLeader() {
      if (this.hasGroupLeader()) {
         this.getNavigator().tryMoveToEntityLiving(this.groupLeader, 1.0D);
      }

   }

   public void func_212810_a(Stream<AbstractGroupFishEntity> p_212810_1_) {
      p_212810_1_.limit((long)(this.getMaxGroupSize() - this.groupSize)).filter((p_212801_1_) -> {
         return p_212801_1_ != this;
      }).forEach((p_212804_1_) -> {
         p_212804_1_.func_212803_a(this);
      });
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
      if (spawnDataIn == null) {
         spawnDataIn = new AbstractGroupFishEntity.GroupData(this);
      } else {
         this.func_212803_a(((AbstractGroupFishEntity.GroupData)spawnDataIn).groupLeader);
      }

      return spawnDataIn;
   }

   public static class GroupData implements ILivingEntityData {
      public final AbstractGroupFishEntity groupLeader;

      public GroupData(AbstractGroupFishEntity groupLeaderIn) {
         this.groupLeader = groupLeaderIn;
      }
   }
}