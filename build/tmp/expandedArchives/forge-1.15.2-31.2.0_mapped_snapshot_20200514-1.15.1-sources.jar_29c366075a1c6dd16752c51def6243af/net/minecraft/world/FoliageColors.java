package net.minecraft.world;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoliageColors {
   private static int[] foliageBuffer = new int[65536];

   public static void setFoliageBiomeColorizer(int[] foliageBufferIn) {
      foliageBuffer = foliageBufferIn;
   }

   public static int get(double temperature, double humidity) {
      humidity = humidity * temperature;
      int i = (int)((1.0D - temperature) * 255.0D);
      int j = (int)((1.0D - humidity) * 255.0D);
      return foliageBuffer[j << 8 | i];
   }

   public static int getSpruce() {
      return 6396257;
   }

   public static int getBirch() {
      return 8431445;
   }

   public static int getDefault() {
      return 4764952;
   }
}