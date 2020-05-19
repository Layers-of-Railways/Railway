package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.world.server.ServerWorld;

public class ChangeJobTask extends Task<VillagerEntity> {
   public ChangeJobTask() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_ABSENT));
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      VillagerData villagerdata = owner.getVillagerData();
      return villagerdata.getProfession() != VillagerProfession.NONE && villagerdata.getProfession() != VillagerProfession.NITWIT && owner.getXp() == 0 && villagerdata.getLevel() <= 1;
   }

   protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      entityIn.setVillagerData(entityIn.getVillagerData().withProfession(VillagerProfession.NONE));
      entityIn.resetBrain(worldIn);
   }
}