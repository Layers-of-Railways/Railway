package net.minecraft.entity.passive.horse;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TraderLlamaEntity extends LlamaEntity {
   private int despawnDelay = 47999;

   public TraderLlamaEntity(EntityType<? extends TraderLlamaEntity> p_i50234_1_, World p_i50234_2_) {
      super(p_i50234_1_, p_i50234_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isTraderLlama() {
      return true;
   }

   protected LlamaEntity createChild() {
      return EntityType.TRADER_LLAMA.create(this.world);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("DespawnDelay", this.despawnDelay);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("DespawnDelay", 99)) {
         this.despawnDelay = compound.getInt("DespawnDelay");
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
      this.targetSelector.addGoal(1, new TraderLlamaEntity.FollowTraderGoal(this));
   }

   protected void mountTo(PlayerEntity player) {
      Entity entity = this.getLeashHolder();
      if (!(entity instanceof WanderingTraderEntity)) {
         super.mountTo(player);
      }
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (!this.world.isRemote) {
         this.tryDespawn();
      }

   }

   private void tryDespawn() {
      if (this.canDespawn()) {
         this.despawnDelay = this.isLeashedToTrader() ? ((WanderingTraderEntity)this.getLeashHolder()).getDespawnDelay() - 1 : this.despawnDelay - 1;
         if (this.despawnDelay <= 0) {
            this.clearLeashed(true, false);
            this.remove();
         }

      }
   }

   private boolean canDespawn() {
      return !this.isTame() && !this.isLeashedToStranger() && !this.isOnePlayerRiding();
   }

   private boolean isLeashedToTrader() {
      return this.getLeashHolder() instanceof WanderingTraderEntity;
   }

   private boolean isLeashedToStranger() {
      return this.getLeashed() && !this.isLeashedToTrader();
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      if (reason == SpawnReason.EVENT) {
         this.setGrowingAge(0);
      }

      if (spawnDataIn == null) {
         spawnDataIn = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)spawnDataIn).func_226259_a_(false);
      }

      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public class FollowTraderGoal extends TargetGoal {
      private final LlamaEntity field_220800_b;
      private LivingEntity field_220801_c;
      private int field_220802_d;

      public FollowTraderGoal(LlamaEntity p_i50458_2_) {
         super(p_i50458_2_, false);
         this.field_220800_b = p_i50458_2_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         if (!this.field_220800_b.getLeashed()) {
            return false;
         } else {
            Entity entity = this.field_220800_b.getLeashHolder();
            if (!(entity instanceof WanderingTraderEntity)) {
               return false;
            } else {
               WanderingTraderEntity wanderingtraderentity = (WanderingTraderEntity)entity;
               this.field_220801_c = wanderingtraderentity.getRevengeTarget();
               int i = wanderingtraderentity.getRevengeTimer();
               return i != this.field_220802_d && this.isSuitableTarget(this.field_220801_c, EntityPredicate.DEFAULT);
            }
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.goalOwner.setAttackTarget(this.field_220801_c);
         Entity entity = this.field_220800_b.getLeashHolder();
         if (entity instanceof WanderingTraderEntity) {
            this.field_220802_d = ((WanderingTraderEntity)entity).getRevengeTimer();
         }

         super.startExecuting();
      }
   }
}