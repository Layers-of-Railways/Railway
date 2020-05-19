package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;

public interface IC0Transformer extends IAreaTransformer1, IDimOffset0Transformer {
   int apply(INoiseRandom context, int value);

   default int func_215728_a(IExtendedNoiseRandom<?> p_215728_1_, IArea p_215728_2_, int p_215728_3_, int p_215728_4_) {
      return this.apply(p_215728_1_, p_215728_2_.getValue(this.func_215721_a(p_215728_3_), this.func_215722_b(p_215728_4_)));
   }
}