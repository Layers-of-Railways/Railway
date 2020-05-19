package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class NearestBedSensor extends Sensor<MobEntity> {
   private final Long2LongMap field_225471_a = new Long2LongOpenHashMap();
   private int field_225472_b;
   private long field_225473_c;

   public NearestBedSensor() {
      super(20);
   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
   }

   protected void update(ServerWorld worldIn, MobEntity entityIn) {
      if (entityIn.isChild()) {
         this.field_225472_b = 0;
         this.field_225473_c = worldIn.getGameTime() + (long)worldIn.getRandom().nextInt(20);
         PointOfInterestManager pointofinterestmanager = worldIn.getPointOfInterestManager();
         Predicate<BlockPos> predicate = (p_225469_1_) -> {
            long i = p_225469_1_.toLong();
            if (this.field_225471_a.containsKey(i)) {
               return false;
            } else if (++this.field_225472_b >= 5) {
               return false;
            } else {
               this.field_225471_a.put(i, this.field_225473_c + 40L);
               return true;
            }
         };
         Stream<BlockPos> stream = pointofinterestmanager.findAll(PointOfInterestType.HOME.getPredicate(), predicate, new BlockPos(entityIn), 48, PointOfInterestManager.Status.ANY);
         Path path = entityIn.getNavigator().func_225463_a(stream, PointOfInterestType.HOME.getValidRange());
         if (path != null && path.reachesTarget()) {
            BlockPos blockpos = path.getTarget();
            Optional<PointOfInterestType> optional = pointofinterestmanager.getType(blockpos);
            if (optional.isPresent()) {
               entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_BED, blockpos);
            }
         } else if (this.field_225472_b < 5) {
            this.field_225471_a.long2LongEntrySet().removeIf((p_225470_1_) -> {
               return p_225470_1_.getLongValue() < this.field_225473_c;
            });
         }

      }
   }
}