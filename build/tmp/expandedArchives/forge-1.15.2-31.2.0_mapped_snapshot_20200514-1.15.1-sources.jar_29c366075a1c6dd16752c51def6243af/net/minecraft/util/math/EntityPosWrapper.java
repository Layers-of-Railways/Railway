package net.minecraft.util.math;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

public class EntityPosWrapper implements IPosWrapper {
   private final Entity entity;

   public EntityPosWrapper(Entity parentScreenIn) {
      this.entity = parentScreenIn;
   }

   public BlockPos getBlockPos() {
      return new BlockPos(this.entity);
   }

   public Vec3d getPos() {
      return new Vec3d(this.entity.getPosX(), this.entity.getPosYEye(), this.entity.getPosZ());
   }

   public boolean isVisibleTo(LivingEntity p_220610_1_) {
      Optional<List<LivingEntity>> optional = p_220610_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
      return this.entity.isAlive() && optional.isPresent() && optional.get().contains(this.entity);
   }

   public String toString() {
      return "EntityPosWrapper for " + this.entity;
   }
}