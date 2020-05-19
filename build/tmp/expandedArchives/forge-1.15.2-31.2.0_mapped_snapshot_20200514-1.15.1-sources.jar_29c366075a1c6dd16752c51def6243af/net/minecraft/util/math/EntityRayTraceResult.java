package net.minecraft.util.math;

import net.minecraft.entity.Entity;

public class EntityRayTraceResult extends RayTraceResult {
   private final Entity entity;

   public EntityRayTraceResult(Entity entityIn) {
      this(entityIn, entityIn.getPositionVec());
   }

   public EntityRayTraceResult(Entity entityIn, Vec3d hitVec) {
      super(hitVec);
      this.entity = entityIn;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public RayTraceResult.Type getType() {
      return RayTraceResult.Type.ENTITY;
   }
}