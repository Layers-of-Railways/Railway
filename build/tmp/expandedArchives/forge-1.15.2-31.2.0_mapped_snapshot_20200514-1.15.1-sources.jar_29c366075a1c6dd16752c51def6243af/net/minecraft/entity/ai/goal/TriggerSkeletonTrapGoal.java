package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.server.ServerWorld;

public class TriggerSkeletonTrapGoal extends Goal {
   private final SkeletonHorseEntity horse;

   public TriggerSkeletonTrapGoal(SkeletonHorseEntity horseIn) {
      this.horse = horseIn;
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.horse.world.isPlayerWithin(this.horse.getPosX(), this.horse.getPosY(), this.horse.getPosZ(), 10.0D);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      DifficultyInstance difficultyinstance = this.horse.world.getDifficultyForLocation(new BlockPos(this.horse));
      this.horse.setTrap(false);
      this.horse.setHorseTamed(true);
      this.horse.setGrowingAge(0);
      ((ServerWorld)this.horse.world).addLightningBolt(new LightningBoltEntity(this.horse.world, this.horse.getPosX(), this.horse.getPosY(), this.horse.getPosZ(), true));
      SkeletonEntity skeletonentity = this.createSkeleton(difficultyinstance, this.horse);
      skeletonentity.startRiding(this.horse);

      for(int i = 0; i < 3; ++i) {
         AbstractHorseEntity abstracthorseentity = this.createHorse(difficultyinstance);
         SkeletonEntity skeletonentity1 = this.createSkeleton(difficultyinstance, abstracthorseentity);
         skeletonentity1.startRiding(abstracthorseentity);
         abstracthorseentity.addVelocity(this.horse.getRNG().nextGaussian() * 0.5D, 0.0D, this.horse.getRNG().nextGaussian() * 0.5D);
      }

   }

   private AbstractHorseEntity createHorse(DifficultyInstance p_188515_1_) {
      SkeletonHorseEntity skeletonhorseentity = EntityType.SKELETON_HORSE.create(this.horse.world);
      skeletonhorseentity.onInitialSpawn(this.horse.world, p_188515_1_, SpawnReason.TRIGGERED, (ILivingEntityData)null, (CompoundNBT)null);
      skeletonhorseentity.setPosition(this.horse.getPosX(), this.horse.getPosY(), this.horse.getPosZ());
      skeletonhorseentity.hurtResistantTime = 60;
      skeletonhorseentity.enablePersistence();
      skeletonhorseentity.setHorseTamed(true);
      skeletonhorseentity.setGrowingAge(0);
      skeletonhorseentity.world.addEntity(skeletonhorseentity);
      return skeletonhorseentity;
   }

   private SkeletonEntity createSkeleton(DifficultyInstance p_188514_1_, AbstractHorseEntity p_188514_2_) {
      SkeletonEntity skeletonentity = EntityType.SKELETON.create(p_188514_2_.world);
      skeletonentity.onInitialSpawn(p_188514_2_.world, p_188514_1_, SpawnReason.TRIGGERED, (ILivingEntityData)null, (CompoundNBT)null);
      skeletonentity.setPosition(p_188514_2_.getPosX(), p_188514_2_.getPosY(), p_188514_2_.getPosZ());
      skeletonentity.hurtResistantTime = 60;
      skeletonentity.enablePersistence();
      if (skeletonentity.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
         skeletonentity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.IRON_HELMET));
      }

      skeletonentity.setItemStackToSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.addRandomEnchantment(skeletonentity.getRNG(), skeletonentity.getHeldItemMainhand(), (int)(5.0F + p_188514_1_.getClampedAdditionalDifficulty() * (float)skeletonentity.getRNG().nextInt(18)), false));
      skeletonentity.setItemStackToSlot(EquipmentSlotType.HEAD, EnchantmentHelper.addRandomEnchantment(skeletonentity.getRNG(), skeletonentity.getItemStackFromSlot(EquipmentSlotType.HEAD), (int)(5.0F + p_188514_1_.getClampedAdditionalDifficulty() * (float)skeletonentity.getRNG().nextInt(18)), false));
      skeletonentity.world.addEntity(skeletonentity);
      return skeletonentity;
   }
}