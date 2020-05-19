package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class GatherPOITask extends Task<CreatureEntity> {
   private final PointOfInterestType field_220604_a;
   private final MemoryModuleType<GlobalPos> field_220605_b;
   private final boolean field_220606_c;
   private long field_220607_d;
   private final Long2LongMap field_223013_e = new Long2LongOpenHashMap();
   private int field_223014_f;

   public GatherPOITask(PointOfInterestType p_i50374_1_, MemoryModuleType<GlobalPos> p_i50374_2_, boolean p_i50374_3_) {
      super(ImmutableMap.of(p_i50374_2_, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220604_a = p_i50374_1_;
      this.field_220605_b = p_i50374_2_;
      this.field_220606_c = p_i50374_3_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
      if (this.field_220606_c && owner.isChild()) {
         return false;
      } else {
         return worldIn.getGameTime() - this.field_220607_d >= 20L;
      }
   }

   protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
      this.field_223014_f = 0;
      this.field_220607_d = worldIn.getGameTime() + (long)worldIn.getRandom().nextInt(20);
      PointOfInterestManager pointofinterestmanager = worldIn.getPointOfInterestManager();
      Predicate<BlockPos> predicate = (p_220603_1_) -> {
         long i = p_220603_1_.toLong();
         if (this.field_223013_e.containsKey(i)) {
            return false;
         } else if (++this.field_223014_f >= 5) {
            return false;
         } else {
            this.field_223013_e.put(i, this.field_220607_d + 40L);
            return true;
         }
      };
      Stream<BlockPos> stream = pointofinterestmanager.findAll(this.field_220604_a.getPredicate(), predicate, new BlockPos(entityIn), 48, PointOfInterestManager.Status.HAS_SPACE);
      Path path = entityIn.getNavigator().func_225463_a(stream, this.field_220604_a.getValidRange());
      if (path != null && path.reachesTarget()) {
         BlockPos blockpos = path.getTarget();
         pointofinterestmanager.getType(blockpos).ifPresent((p_225441_5_) -> {
            pointofinterestmanager.take(this.field_220604_a.getPredicate(), (p_225442_1_) -> {
               return p_225442_1_.equals(blockpos);
            }, blockpos, 1);
            entityIn.getBrain().setMemory(this.field_220605_b, GlobalPos.of(worldIn.getDimension().getType(), blockpos));
            DebugPacketSender.func_218801_c(worldIn, blockpos);
         });
      } else if (this.field_223014_f < 5) {
         this.field_223013_e.long2LongEntrySet().removeIf((p_223011_1_) -> {
            return p_223011_1_.getLongValue() < this.field_220607_d;
         });
      }

   }
}