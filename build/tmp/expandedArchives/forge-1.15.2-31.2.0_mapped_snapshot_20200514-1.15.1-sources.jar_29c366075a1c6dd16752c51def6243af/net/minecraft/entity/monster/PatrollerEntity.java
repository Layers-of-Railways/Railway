package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.raid.Raid;

public abstract class PatrollerEntity extends MonsterEntity {
   private BlockPos patrolTarget;
   private boolean patrolLeader;
   private boolean patrolling;

   protected PatrollerEntity(EntityType<? extends PatrollerEntity> p_i50201_1_, World worldIn) {
      super(p_i50201_1_, worldIn);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(4, new PatrollerEntity.PatrolGoal<>(this, 0.7D, 0.595D));
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      if (this.patrolTarget != null) {
         compound.put("PatrolTarget", NBTUtil.writeBlockPos(this.patrolTarget));
      }

      compound.putBoolean("PatrolLeader", this.patrolLeader);
      compound.putBoolean("Patrolling", this.patrolling);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("PatrolTarget")) {
         this.patrolTarget = NBTUtil.readBlockPos(compound.getCompound("PatrolTarget"));
      }

      this.patrolLeader = compound.getBoolean("PatrolLeader");
      this.patrolling = compound.getBoolean("Patrolling");
   }

   /**
    * Returns the Y Offset of this entity.
    */
   public double getYOffset() {
      return -0.45D;
   }

   public boolean canBeLeader() {
      return true;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      if (reason != SpawnReason.PATROL && reason != SpawnReason.EVENT && reason != SpawnReason.STRUCTURE && this.rand.nextFloat() < 0.06F && this.canBeLeader()) {
         this.patrolLeader = true;
      }

      if (this.isLeader()) {
         this.setItemStackToSlot(EquipmentSlotType.HEAD, Raid.createIllagerBanner());
         this.setDropChance(EquipmentSlotType.HEAD, 2.0F);
      }

      if (reason == SpawnReason.PATROL) {
         this.patrolling = true;
      }

      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public static boolean func_223330_b(EntityType<? extends PatrollerEntity> patrollerType, IWorld worldIn, SpawnReason reason, BlockPos p_223330_3_, Random p_223330_4_) {
      return worldIn.getLightFor(LightType.BLOCK, p_223330_3_) > 8 ? false : canMonsterSpawn(patrollerType, worldIn, reason, p_223330_3_, p_223330_4_);
   }

   public boolean canDespawn(double distanceToClosestPlayer) {
      return !this.patrolling || distanceToClosestPlayer > 16384.0D;
   }

   public void setPatrolTarget(BlockPos p_213631_1_) {
      this.patrolTarget = p_213631_1_;
      this.patrolling = true;
   }

   public BlockPos getPatrolTarget() {
      return this.patrolTarget;
   }

   public boolean hasPatrolTarget() {
      return this.patrolTarget != null;
   }

   public void setLeader(boolean isLeader) {
      this.patrolLeader = isLeader;
      this.patrolling = true;
   }

   public boolean isLeader() {
      return this.patrolLeader;
   }

   public boolean func_213634_ed() {
      return true;
   }

   public void resetPatrolTarget() {
      this.patrolTarget = (new BlockPos(this)).add(-500 + this.rand.nextInt(1000), 0, -500 + this.rand.nextInt(1000));
      this.patrolling = true;
   }

   protected boolean isPatrolling() {
      return this.patrolling;
   }

   protected void setPatrolling(boolean p_226541_1_) {
      this.patrolling = p_226541_1_;
   }

   public static class PatrolGoal<T extends PatrollerEntity> extends Goal {
      private final T owner;
      private final double field_220840_b;
      private final double field_220841_c;
      private long field_226542_d_;

      public PatrolGoal(T p_i50070_1_, double p_i50070_2_, double p_i50070_4_) {
         this.owner = p_i50070_1_;
         this.field_220840_b = p_i50070_2_;
         this.field_220841_c = p_i50070_4_;
         this.field_226542_d_ = -1L;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         boolean flag = this.owner.world.getGameTime() < this.field_226542_d_;
         return this.owner.isPatrolling() && this.owner.getAttackTarget() == null && !this.owner.isBeingRidden() && this.owner.hasPatrolTarget() && !flag;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         boolean flag = this.owner.isLeader();
         PathNavigator pathnavigator = this.owner.getNavigator();
         if (pathnavigator.noPath()) {
            List<PatrollerEntity> list = this.func_226544_g_();
            if (this.owner.isPatrolling() && list.isEmpty()) {
               this.owner.setPatrolling(false);
            } else if (flag && this.owner.getPatrolTarget().withinDistance(this.owner.getPositionVec(), 10.0D)) {
               this.owner.resetPatrolTarget();
            } else {
               Vec3d vec3d = new Vec3d(this.owner.getPatrolTarget());
               Vec3d vec3d1 = this.owner.getPositionVec();
               Vec3d vec3d2 = vec3d1.subtract(vec3d);
               vec3d = vec3d2.rotateYaw(90.0F).scale(0.4D).add(vec3d);
               Vec3d vec3d3 = vec3d.subtract(vec3d1).normalize().scale(10.0D).add(vec3d1);
               BlockPos blockpos = new BlockPos(vec3d3);
               blockpos = this.owner.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos);
               if (!pathnavigator.tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), flag ? this.field_220841_c : this.field_220840_b)) {
                  this.func_226545_h_();
                  this.field_226542_d_ = this.owner.world.getGameTime() + 200L;
               } else if (flag) {
                  for(PatrollerEntity patrollerentity : list) {
                     patrollerentity.setPatrolTarget(blockpos);
                  }
               }
            }
         }

      }

      private List<PatrollerEntity> func_226544_g_() {
         return this.owner.world.getEntitiesWithinAABB(PatrollerEntity.class, this.owner.getBoundingBox().grow(16.0D), (p_226543_1_) -> {
            return p_226543_1_.func_213634_ed() && !p_226543_1_.isEntityEqual(this.owner);
         });
      }

      private boolean func_226545_h_() {
         Random random = this.owner.getRNG();
         BlockPos blockpos = this.owner.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.owner)).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
         return this.owner.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), this.field_220840_b);
      }
   }
}