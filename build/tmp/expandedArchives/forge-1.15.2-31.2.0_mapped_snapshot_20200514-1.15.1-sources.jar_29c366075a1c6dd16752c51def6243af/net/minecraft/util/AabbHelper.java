package net.minecraft.util;

import net.minecraft.util.math.AxisAlignedBB;

public class AabbHelper {
   public static AxisAlignedBB func_227019_a_(AxisAlignedBB p_227019_0_, Direction p_227019_1_, double p_227019_2_) {
      double d0 = p_227019_2_ * (double)p_227019_1_.getAxisDirection().getOffset();
      double d1 = Math.min(d0, 0.0D);
      double d2 = Math.max(d0, 0.0D);
      switch(p_227019_1_) {
      case WEST:
         return new AxisAlignedBB(p_227019_0_.minX + d1, p_227019_0_.minY, p_227019_0_.minZ, p_227019_0_.minX + d2, p_227019_0_.maxY, p_227019_0_.maxZ);
      case EAST:
         return new AxisAlignedBB(p_227019_0_.maxX + d1, p_227019_0_.minY, p_227019_0_.minZ, p_227019_0_.maxX + d2, p_227019_0_.maxY, p_227019_0_.maxZ);
      case DOWN:
         return new AxisAlignedBB(p_227019_0_.minX, p_227019_0_.minY + d1, p_227019_0_.minZ, p_227019_0_.maxX, p_227019_0_.minY + d2, p_227019_0_.maxZ);
      case UP:
      default:
         return new AxisAlignedBB(p_227019_0_.minX, p_227019_0_.maxY + d1, p_227019_0_.minZ, p_227019_0_.maxX, p_227019_0_.maxY + d2, p_227019_0_.maxZ);
      case NORTH:
         return new AxisAlignedBB(p_227019_0_.minX, p_227019_0_.minY, p_227019_0_.minZ + d1, p_227019_0_.maxX, p_227019_0_.maxY, p_227019_0_.minZ + d2);
      case SOUTH:
         return new AxisAlignedBB(p_227019_0_.minX, p_227019_0_.minY, p_227019_0_.maxZ + d1, p_227019_0_.maxX, p_227019_0_.maxY, p_227019_0_.maxZ + d2);
      }
   }
}