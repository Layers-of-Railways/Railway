package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class SecondaryPositionSensor extends Sensor<VillagerEntity> {
   public SecondaryPositionSensor() {
      super(40);
   }

   protected void update(ServerWorld worldIn, VillagerEntity entityIn) {
      DimensionType dimensiontype = worldIn.getDimension().getType();
      BlockPos blockpos = new BlockPos(entityIn);
      List<GlobalPos> list = Lists.newArrayList();
      int i = 4;

      for(int j = -4; j <= 4; ++j) {
         for(int k = -2; k <= 2; ++k) {
            for(int l = -4; l <= 4; ++l) {
               BlockPos blockpos1 = blockpos.add(j, k, l);
               if (entityIn.getVillagerData().getProfession().getRelatedWorldBlocks().contains(worldIn.getBlockState(blockpos1).getBlock())) {
                  list.add(GlobalPos.of(dimensiontype, blockpos1));
               }
            }
         }
      }

      Brain<?> brain = entityIn.getBrain();
      if (!list.isEmpty()) {
         brain.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, list);
      } else {
         brain.removeMemory(MemoryModuleType.SECONDARY_JOB_SITE);
      }

   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
   }
}