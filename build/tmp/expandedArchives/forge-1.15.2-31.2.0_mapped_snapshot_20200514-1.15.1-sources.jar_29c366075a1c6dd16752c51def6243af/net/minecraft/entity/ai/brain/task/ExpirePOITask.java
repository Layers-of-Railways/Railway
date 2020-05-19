package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class ExpirePOITask extends Task<LivingEntity> {
   private final MemoryModuleType<GlobalPos> field_220591_a;
   private final Predicate<PointOfInterestType> field_220592_b;

   public ExpirePOITask(PointOfInterestType p_i50338_1_, MemoryModuleType<GlobalPos> p_i50338_2_) {
      super(ImmutableMap.of(p_i50338_2_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220592_b = p_i50338_1_.getPredicate();
      this.field_220591_a = p_i50338_2_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      GlobalPos globalpos = owner.getBrain().getMemory(this.field_220591_a).get();
      return Objects.equals(worldIn.getDimension().getType(), globalpos.getDimension()) && globalpos.getPos().withinDistance(owner.getPositionVec(), 5.0D);
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      GlobalPos globalpos = brain.getMemory(this.field_220591_a).get();
      BlockPos blockpos = globalpos.getPos();
      ServerWorld serverworld = worldIn.getServer().getWorld(globalpos.getDimension());
      if (this.func_223020_a(serverworld, blockpos)) {
         brain.removeMemory(this.field_220591_a);
      } else if (this.func_223019_a(serverworld, blockpos, entityIn)) {
         brain.removeMemory(this.field_220591_a);
         worldIn.getPointOfInterestManager().release(blockpos);
         DebugPacketSender.func_218801_c(worldIn, blockpos);
      }

   }

   private boolean func_223019_a(ServerWorld p_223019_1_, BlockPos p_223019_2_, LivingEntity p_223019_3_) {
      BlockState blockstate = p_223019_1_.getBlockState(p_223019_2_);
      return blockstate.getBlock().isIn(BlockTags.BEDS) && blockstate.get(BedBlock.OCCUPIED) && !p_223019_3_.isSleeping();
   }

   private boolean func_223020_a(ServerWorld p_223020_1_, BlockPos p_223020_2_) {
      return !p_223020_1_.getPointOfInterestManager().exists(p_223020_2_, this.field_220592_b);
   }
}