package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ParticleStatus {
   ALL(0, "options.particles.all"),
   DECREASED(1, "options.particles.decreased"),
   MINIMAL(2, "options.particles.minimal");

   private static final ParticleStatus[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(ParticleStatus::getId)).toArray((p_216834_0_) -> {
      return new ParticleStatus[p_216834_0_];
   });
   private final int id;
   private final String resourceKey;

   private ParticleStatus(int id, String resourceKeyIn) {
      this.id = id;
      this.resourceKey = resourceKeyIn;
   }

   public String getResourceKey() {
      return this.resourceKey;
   }

   public int getId() {
      return this.id;
   }

   public static ParticleStatus byId(int id) {
      return BY_ID[MathHelper.normalizeAngle(id, BY_ID.length)];
   }
}