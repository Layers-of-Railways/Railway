package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer1 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context, IAreaFactory<R> areaFactory) {
      return () -> {
         R r = areaFactory.make();
         return context.func_212859_a_((p_202711_3_, p_202711_4_) -> {
            context.setPosition((long)p_202711_3_, (long)p_202711_4_);
            return this.func_215728_a(context, r, p_202711_3_, p_202711_4_);
         }, r);
      };
   }

   int func_215728_a(IExtendedNoiseRandom<?> p_215728_1_, IArea p_215728_2_, int p_215728_3_, int p_215728_4_);
}