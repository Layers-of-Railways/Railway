package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class CreateBabyVillagerTask extends Task<VillagerEntity> {
   private long field_220483_a;

   public CreateBabyVillagerTask() {
      super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT), 350, 350);
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      return this.func_220478_b(owner);
   }

   protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      return gameTimeIn <= this.field_220483_a && this.func_220478_b(entityIn);
   }

   protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      VillagerEntity villagerentity = this.getBreedTarget(entityIn);
      BrainUtil.lookApproachEachOther(entityIn, villagerentity);
      worldIn.setEntityState(villagerentity, (byte)18);
      worldIn.setEntityState(entityIn, (byte)18);
      int i = 275 + entityIn.getRNG().nextInt(50);
      this.field_220483_a = gameTimeIn + (long)i;
   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      VillagerEntity villagerentity = this.getBreedTarget(owner);
      if (!(owner.getDistanceSq(villagerentity) > 5.0D)) {
         BrainUtil.lookApproachEachOther(owner, villagerentity);
         if (gameTime >= this.field_220483_a) {
            owner.func_223346_ep();
            villagerentity.func_223346_ep();
            this.func_223521_a(worldIn, owner, villagerentity);
         } else if (owner.getRNG().nextInt(35) == 0) {
            worldIn.setEntityState(villagerentity, (byte)12);
            worldIn.setEntityState(owner, (byte)12);
         }

      }
   }

   private void func_223521_a(ServerWorld p_223521_1_, VillagerEntity p_223521_2_, VillagerEntity p_223521_3_) {
      Optional<BlockPos> optional = this.func_220479_b(p_223521_1_, p_223521_2_);
      if (!optional.isPresent()) {
         p_223521_1_.setEntityState(p_223521_3_, (byte)13);
         p_223521_1_.setEntityState(p_223521_2_, (byte)13);
      } else {
         Optional<VillagerEntity> optional1 = this.func_220480_a(p_223521_2_, p_223521_3_);
         if (optional1.isPresent()) {
            this.func_220477_a(p_223521_1_, optional1.get(), optional.get());
         } else {
            p_223521_1_.getPointOfInterestManager().release(optional.get());
            DebugPacketSender.func_218801_c(p_223521_1_, optional.get());
         }
      }

   }

   protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      entityIn.getBrain().removeMemory(MemoryModuleType.BREED_TARGET);
   }

   private VillagerEntity getBreedTarget(VillagerEntity p_220482_1_) {
      return p_220482_1_.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
   }

   private boolean func_220478_b(VillagerEntity p_220478_1_) {
      Brain<VillagerEntity> brain = p_220478_1_.getBrain();
      if (!brain.getMemory(MemoryModuleType.BREED_TARGET).isPresent()) {
         return false;
      } else {
         VillagerEntity villagerentity = this.getBreedTarget(p_220478_1_);
         return BrainUtil.isCorrectVisibleType(brain, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && p_220478_1_.canBreed() && villagerentity.canBreed();
      }
   }

   private Optional<BlockPos> func_220479_b(ServerWorld p_220479_1_, VillagerEntity p_220479_2_) {
      return p_220479_1_.getPointOfInterestManager().take(PointOfInterestType.HOME.getPredicate(), (p_220481_2_) -> {
         return this.func_223520_a(p_220479_2_, p_220481_2_);
      }, new BlockPos(p_220479_2_), 48);
   }

   private boolean func_223520_a(VillagerEntity p_223520_1_, BlockPos p_223520_2_) {
      Path path = p_223520_1_.getNavigator().getPathToPos(p_223520_2_, PointOfInterestType.HOME.getValidRange());
      return path != null && path.reachesTarget();
   }

   private Optional<VillagerEntity> func_220480_a(VillagerEntity p_220480_1_, VillagerEntity p_220480_2_) {
      VillagerEntity villagerentity = p_220480_1_.createChild(p_220480_2_);
      if (villagerentity == null) {
         return Optional.empty();
      } else {
         p_220480_1_.setGrowingAge(6000);
         p_220480_2_.setGrowingAge(6000);
         villagerentity.setGrowingAge(-24000);
         villagerentity.setLocationAndAngles(p_220480_1_.getPosX(), p_220480_1_.getPosY(), p_220480_1_.getPosZ(), 0.0F, 0.0F);
         p_220480_1_.world.addEntity(villagerentity);
         p_220480_1_.world.setEntityState(villagerentity, (byte)12);
         return Optional.of(villagerentity);
      }
   }

   private void func_220477_a(ServerWorld p_220477_1_, VillagerEntity p_220477_2_, BlockPos p_220477_3_) {
      GlobalPos globalpos = GlobalPos.of(p_220477_1_.getDimension().getType(), p_220477_3_);
      p_220477_2_.getBrain().setMemory(MemoryModuleType.HOME, globalpos);
   }
}