package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CaveSpiderEntity extends SpiderEntity {
   public CaveSpiderEntity(EntityType<? extends CaveSpiderEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (super.attackEntityAsMob(entityIn)) {
         if (entityIn instanceof LivingEntity) {
            int i = 0;
            if (this.world.getDifficulty() == Difficulty.NORMAL) {
               i = 7;
            } else if (this.world.getDifficulty() == Difficulty.HARD) {
               i = 15;
            }

            if (i > 0) {
               ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      return spawnDataIn;
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 0.45F;
   }
}