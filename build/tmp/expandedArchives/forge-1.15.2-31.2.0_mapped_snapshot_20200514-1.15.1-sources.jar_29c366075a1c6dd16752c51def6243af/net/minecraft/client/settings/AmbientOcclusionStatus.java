package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum AmbientOcclusionStatus {
   OFF(0, "options.ao.off"),
   MIN(1, "options.ao.min"),
   MAX(2, "options.ao.max");

   private static final AmbientOcclusionStatus[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(AmbientOcclusionStatus::getId)).toArray((p_216571_0_) -> {
      return new AmbientOcclusionStatus[p_216571_0_];
   });
   private final int id;
   private final String resourceKey;

   private AmbientOcclusionStatus(int idIn, String resourceKeyIn) {
      this.id = idIn;
      this.resourceKey = resourceKeyIn;
   }

   public int getId() {
      return this.id;
   }

   public String getResourceKey() {
      return this.resourceKey;
   }

   public static AmbientOcclusionStatus getValue(int valueIn) {
      return VALUES[MathHelper.normalizeAngle(valueIn, VALUES.length)];
   }
}