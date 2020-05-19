package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class InteractableDoorsSensor extends Sensor<LivingEntity> {
   protected void update(ServerWorld worldIn, LivingEntity entityIn) {
      DimensionType dimensiontype = worldIn.getDimension().getType();
      BlockPos blockpos = new BlockPos(entityIn);
      List<GlobalPos> list = Lists.newArrayList();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               BlockPos blockpos1 = blockpos.add(i, j, k);
               if (worldIn.getBlockState(blockpos1).isIn(BlockTags.WOODEN_DOORS)) {
                  list.add(GlobalPos.of(dimensiontype, blockpos1));
               }
            }
         }
      }

      Brain<?> brain = entityIn.getBrain();
      if (!list.isEmpty()) {
         brain.setMemory(MemoryModuleType.INTERACTABLE_DOORS, list);
      } else {
         brain.removeMemory(MemoryModuleType.INTERACTABLE_DOORS);
      }

   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.INTERACTABLE_DOORS);
   }
}