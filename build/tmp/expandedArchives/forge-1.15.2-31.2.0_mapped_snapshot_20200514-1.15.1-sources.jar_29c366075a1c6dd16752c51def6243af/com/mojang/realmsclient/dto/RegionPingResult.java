package com.mojang.realmsclient.dto;

import java.util.Locale;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegionPingResult extends ValueObject {
   private final String regionName;
   private final int ping;

   public RegionPingResult(String entityRendererIn, int mcIn) {
      this.regionName = entityRendererIn;
      this.ping = mcIn;
   }

   public int ping() {
      return this.ping;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s --> %.2f ms", this.regionName, (float)this.ping);
   }
}