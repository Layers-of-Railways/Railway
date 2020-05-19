package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;

public interface IBishopTransformer extends IAreaTransformer1, IDimOffset1Transformer {
   int apply(INoiseRandom context, int x, int p_202792_3_, int p_202792_4_, int p_202792_5_, int p_202792_6_);

   default int func_215728_a(IExtendedNoiseRandom<?> p_215728_1_, IArea p_215728_2_, int p_215728_3_, int p_215728_4_) {
      return this.apply(p_215728_1_, p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 0), this.func_215722_b(p_215728_4_ + 2)), p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 2), this.func_215722_b(p_215728_4_ + 2)), p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 2), this.func_215722_b(p_215728_4_ + 0)), p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 0), this.func_215722_b(p_215728_4_ + 0)), p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 1), this.func_215722_b(p_215728_4_ + 1)));
   }
}