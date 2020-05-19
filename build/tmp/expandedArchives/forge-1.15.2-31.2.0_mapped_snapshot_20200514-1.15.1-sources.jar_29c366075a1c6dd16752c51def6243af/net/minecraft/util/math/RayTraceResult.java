package net.minecraft.util.math;

public abstract class RayTraceResult {
   protected final Vec3d hitResult;

   protected RayTraceResult(Vec3d hitVec) {
      this.hitResult = hitVec;
   }

   public abstract RayTraceResult.Type getType();
   /** Used to determine what sub-segment is hit */
   public int subHit = -1;

   /** Used to add extra hit info */
   public Object hitInfo = null;

   /**
    * Returns the hit position of the raycast, in absolute world coordinates
    */
   public Vec3d getHitVec() {
      return this.hitResult;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}