package net.minecraft.entity.ai.brain;

import java.util.Comparator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class BrainUtil {
   public static void lookApproachEachOther(LivingEntity firstEntity, LivingEntity secondEntity) {
      lookAtEachOther(firstEntity, secondEntity);
      approachEachOther(firstEntity, secondEntity);
   }

   public static boolean canSee(Brain<?> brainIn, LivingEntity target) {
      return brainIn.getMemory(MemoryModuleType.VISIBLE_MOBS).filter((p_220614_1_) -> {
         return p_220614_1_.contains(target);
      }).isPresent();
   }

   public static boolean isCorrectVisibleType(Brain<?> brains, MemoryModuleType<? extends LivingEntity> memorymodule, EntityType<?> entityTypeIn) {
      return brains.getMemory(memorymodule).filter((p_220622_1_) -> {
         return p_220622_1_.getType() == entityTypeIn;
      }).filter(LivingEntity::isAlive).filter((p_220615_1_) -> {
         return canSee(brains, p_220615_1_);
      }).isPresent();
   }

   public static void lookAtEachOther(LivingEntity firstEntity, LivingEntity secondEntity) {
      lookAt(firstEntity, secondEntity);
      lookAt(secondEntity, firstEntity);
   }

   public static void lookAt(LivingEntity entityIn, LivingEntity targetIn) {
      entityIn.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(targetIn));
   }

   public static void approachEachOther(LivingEntity firstEntity, LivingEntity secondEntity) {
      int i = 2;
      approach(firstEntity, secondEntity, 2);
      approach(secondEntity, firstEntity, 2);
   }

   public static void approach(LivingEntity living, LivingEntity target, int targetDistance) {
      float f = (float)living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
      EntityPosWrapper entityposwrapper = new EntityPosWrapper(target);
      WalkTarget walktarget = new WalkTarget(entityposwrapper, f, targetDistance);
      living.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, entityposwrapper);
      living.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   public static void throwItemAt(LivingEntity from, ItemStack stack, LivingEntity to) {
      double d0 = from.getPosYEye() - (double)0.3F;
      ItemEntity itementity = new ItemEntity(from.world, from.getPosX(), d0, from.getPosZ(), stack);
      BlockPos blockpos = new BlockPos(to);
      BlockPos blockpos1 = new BlockPos(from);
      float f = 0.3F;
      Vec3d vec3d = new Vec3d(blockpos.subtract(blockpos1));
      vec3d = vec3d.normalize().scale((double)0.3F);
      itementity.setMotion(vec3d);
      itementity.setDefaultPickupDelay();
      from.world.addEntity(itementity);
   }

   public static SectionPos func_220617_a(ServerWorld serverWorldIn, SectionPos sectionPosIn, int radius) {
      int i = serverWorldIn.sectionsToVillage(sectionPosIn);
      return SectionPos.getAllInBox(sectionPosIn, radius).filter((p_220620_2_) -> {
         return serverWorldIn.sectionsToVillage(p_220620_2_) < i;
      }).min(Comparator.comparingInt(serverWorldIn::sectionsToVillage)).orElse(sectionPosIn);
   }
}