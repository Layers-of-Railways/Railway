package net.minecraft.entity.ai.brain.memory;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.math.Vec3d;

public class WalkTarget {
   private final IPosWrapper target;
   private final float speed;
   private final int distance;

   public WalkTarget(BlockPos targetIn, float speedIn, int distanceIn) {
      this(new BlockPosWrapper(targetIn), speedIn, distanceIn);
   }

   public WalkTarget(Vec3d targetIn, float speedIn, int distanceIn) {
      this(new BlockPosWrapper(new BlockPos(targetIn)), speedIn, distanceIn);
   }

   public WalkTarget(IPosWrapper targetIn, float speedIn, int distanceIn) {
      this.target = targetIn;
      this.speed = speedIn;
      this.distance = distanceIn;
   }

   public IPosWrapper getTarget() {
      return this.target;
   }

   public float getSpeed() {
      return this.speed;
   }

   public int getDistance() {
      return this.distance;
   }
}