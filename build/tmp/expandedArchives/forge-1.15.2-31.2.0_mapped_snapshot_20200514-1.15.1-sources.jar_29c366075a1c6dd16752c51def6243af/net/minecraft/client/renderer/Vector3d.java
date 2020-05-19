package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Vector3d {
   /** The X coordinate */
   public double x;
   /** The Y coordinate */
   public double y;
   /** The Z coordinate */
   public double z;

   public Vector3d(double xIn, double yIn, double zIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
   }
}