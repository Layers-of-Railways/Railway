package net.minecraft.world;

public enum LightType {
   SKY(15),
   BLOCK(0);

   public final int defaultLightValue;

   private LightType(int defaultLightValueIn) {
      this.defaultLightValue = defaultLightValueIn;
   }
}