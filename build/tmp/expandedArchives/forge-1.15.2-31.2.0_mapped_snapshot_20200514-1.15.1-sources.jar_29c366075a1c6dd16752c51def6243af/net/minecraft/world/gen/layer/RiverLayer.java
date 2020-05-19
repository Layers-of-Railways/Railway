package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum RiverLayer implements ICastleTransformer {
   INSTANCE;

   public static final int RIVER = Registry.BIOME.getId(Biomes.RIVER);

   public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
      int i = riverFilter(center);
      return i == riverFilter(east) && i == riverFilter(north) && i == riverFilter(west) && i == riverFilter(south) ? -1 : RIVER;
   }

   private static int riverFilter(int p_151630_0_) {
      return p_151630_0_ >= 2 ? 2 + (p_151630_0_ & 1) : p_151630_0_;
   }
}