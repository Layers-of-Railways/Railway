package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class WalkToHouseTask extends Task<LivingEntity> {
   private final float field_220524_a;
   private final Long2LongMap field_225455_b = new Long2LongOpenHashMap();
   private int field_225456_c;
   private long field_220525_b;

   public WalkToHouseTask(float p_i50353_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220524_a = p_i50353_1_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      if (worldIn.getGameTime() - this.field_220525_b < 20L) {
         return false;
      } else {
         CreatureEntity creatureentity = (CreatureEntity)owner;
         PointOfInterestManager pointofinterestmanager = worldIn.getPointOfInterestManager();
         Optional<BlockPos> optional = pointofinterestmanager.findClosest(PointOfInterestType.HOME.getPredicate(), (p_220522_0_) -> {
            return true;
         }, new BlockPos(owner), 48, PointOfInterestManager.Status.ANY);
         return optional.isPresent() && !(optional.get().distanceSq(new BlockPos(creatureentity)) <= 4.0D);
      }
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      this.field_225456_c = 0;
      this.field_220525_b = worldIn.getGameTime() + (long)worldIn.getRandom().nextInt(20);
      CreatureEntity creatureentity = (CreatureEntity)entityIn;
      PointOfInterestManager pointofinterestmanager = worldIn.getPointOfInterestManager();
      Predicate<BlockPos> predicate = (p_225453_1_) -> {
         long i = p_225453_1_.toLong();
         if (this.field_225455_b.containsKey(i)) {
            return false;
         } else if (++this.field_225456_c >= 5) {
            return false;
         } else {
            this.field_225455_b.put(i, this.field_220525_b + 40L);
            return true;
         }
      };
      Stream<BlockPos> stream = pointofinterestmanager.findAll(PointOfInterestType.HOME.getPredicate(), predicate, new BlockPos(entityIn), 48, PointOfInterestManager.Status.ANY);
      Path path = creatureentity.getNavigator().func_225463_a(stream, PointOfInterestType.HOME.getValidRange());
      if (path != null && path.reachesTarget()) {
         BlockPos blockpos = path.getTarget();
         Optional<PointOfInterestType> optional = pointofinterestmanager.getType(blockpos);
         if (optional.isPresent()) {
            entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockpos, this.field_220524_a, 1));
            DebugPacketSender.func_218801_c(worldIn, blockpos);
         }
      } else if (this.field_225456_c < 5) {
         this.field_225455_b.long2LongEntrySet().removeIf((p_225454_1_) -> {
            return p_225454_1_.getLongValue() < this.field_220525_b;
         });
      }

   }
}