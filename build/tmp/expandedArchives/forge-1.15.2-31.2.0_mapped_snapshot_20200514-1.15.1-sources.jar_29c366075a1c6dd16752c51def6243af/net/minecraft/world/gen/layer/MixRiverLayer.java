package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MixRiverLayer implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   private static final int FROZEN_RIVER = Registry.BIOME.getId(Biomes.FROZEN_RIVER);
   private static final int SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
   private static final int MUSHROOM_FIELD_SHORE = Registry.BIOME.getId(Biomes.MUSHROOM_FIELD_SHORE);
   private static final int RIVER = Registry.BIOME.getId(Biomes.RIVER);

   public int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
      int i = p_215723_2_.getValue(this.func_215721_a(p_215723_4_), this.func_215722_b(p_215723_5_));
      int j = p_215723_3_.getValue(this.func_215721_a(p_215723_4_), this.func_215722_b(p_215723_5_));
      if (LayerUtil.isOcean(i)) {
         return i;
      } else if (j == RIVER) {
         return Registry.BIOME.getId(Registry.BIOME.getByValue(i).getRiver());
      } else {
         return i;
      }
   }
}