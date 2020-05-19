package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class AssignProfessionTask extends Task<VillagerEntity> {
   public AssignProfessionTask() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      return owner.getVillagerData().getProfession() == VillagerProfession.NONE;
   }

   protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      GlobalPos globalpos = entityIn.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
      MinecraftServer minecraftserver = worldIn.getServer();
      minecraftserver.getWorld(globalpos.getDimension()).getPointOfInterestManager().getType(globalpos.getPos()).ifPresent((p_220390_2_) -> {
         Registry.VILLAGER_PROFESSION.stream().filter((p_220389_1_) -> {
            return p_220389_1_.getPointOfInterest() == p_220390_2_;
         }).findFirst().ifPresent((p_220388_2_) -> {
            entityIn.setVillagerData(entityIn.getVillagerData().withProfession(p_220388_2_));
            entityIn.resetBrain(worldIn);
         });
      });
   }
}