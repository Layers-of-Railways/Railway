package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class JumpOnBedTask extends Task<MobEntity> {
   private final float field_220470_a;
   @Nullable
   private BlockPos bedPos;
   private int field_220472_c;
   private int field_220473_d;
   private int field_220474_e;

   public JumpOnBedTask(float p_i50362_1_) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220470_a = p_i50362_1_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, MobEntity owner) {
      return owner.isChild() && this.func_220469_b(worldIn, owner);
   }

   protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
      super.startExecuting(worldIn, entityIn, gameTimeIn);
      this.getBed(entityIn).ifPresent((p_220461_3_) -> {
         this.bedPos = p_220461_3_;
         this.field_220472_c = 100;
         this.field_220473_d = 3 + worldIn.rand.nextInt(4);
         this.field_220474_e = 0;
         this.func_220467_a(entityIn, p_220461_3_);
      });
   }

   protected void resetTask(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
      super.resetTask(worldIn, entityIn, gameTimeIn);
      this.bedPos = null;
      this.field_220472_c = 0;
      this.field_220473_d = 0;
      this.field_220474_e = 0;
   }

   protected boolean shouldContinueExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
      return entityIn.isChild() && this.bedPos != null && this.func_220466_a(worldIn, this.bedPos) && !this.func_220464_e(worldIn, entityIn) && !this.func_220462_f(worldIn, entityIn);
   }

   protected boolean isTimedOut(long gameTime) {
      return false;
   }

   protected void updateTask(ServerWorld worldIn, MobEntity owner, long gameTime) {
      if (!this.func_220468_c(worldIn, owner)) {
         --this.field_220472_c;
      } else if (this.field_220474_e > 0) {
         --this.field_220474_e;
      } else {
         if (this.func_220465_d(worldIn, owner)) {
            owner.getJumpController().setJumping();
            --this.field_220473_d;
            this.field_220474_e = 5;
         }

      }
   }

   private void func_220467_a(MobEntity p_220467_1_, BlockPos p_220467_2_) {
      p_220467_1_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220467_2_, this.field_220470_a, 0));
   }

   private boolean func_220469_b(ServerWorld p_220469_1_, MobEntity p_220469_2_) {
      return this.func_220468_c(p_220469_1_, p_220469_2_) || this.getBed(p_220469_2_).isPresent();
   }

   private boolean func_220468_c(ServerWorld p_220468_1_, MobEntity p_220468_2_) {
      BlockPos blockpos = new BlockPos(p_220468_2_);
      BlockPos blockpos1 = blockpos.down();
      return this.func_220466_a(p_220468_1_, blockpos) || this.func_220466_a(p_220468_1_, blockpos1);
   }

   private boolean func_220465_d(ServerWorld p_220465_1_, MobEntity p_220465_2_) {
      return this.func_220466_a(p_220465_1_, new BlockPos(p_220465_2_));
   }

   private boolean func_220466_a(ServerWorld p_220466_1_, BlockPos p_220466_2_) {
      return p_220466_1_.getBlockState(p_220466_2_).isIn(BlockTags.BEDS);
   }

   private Optional<BlockPos> getBed(MobEntity p_220463_1_) {
      return p_220463_1_.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
   }

   private boolean func_220464_e(ServerWorld p_220464_1_, MobEntity p_220464_2_) {
      return !this.func_220468_c(p_220464_1_, p_220464_2_) && this.field_220472_c <= 0;
   }

   private boolean func_220462_f(ServerWorld p_220462_1_, MobEntity p_220462_2_) {
      return this.func_220468_c(p_220462_1_, p_220462_2_) && this.field_220473_d <= 0;
   }
}