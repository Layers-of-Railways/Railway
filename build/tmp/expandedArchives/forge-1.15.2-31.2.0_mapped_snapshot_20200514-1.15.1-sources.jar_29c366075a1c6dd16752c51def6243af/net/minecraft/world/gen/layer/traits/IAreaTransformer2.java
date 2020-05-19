package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer2 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context, IAreaFactory<R> areaFactory, IAreaFactory<R> areaFactoryIn) {
      return () -> {
         R r = areaFactory.make();
         R r1 = areaFactoryIn.make();
         return context.makeArea((p_215724_4_, p_215724_5_) -> {
            context.setPosition((long)p_215724_4_, (long)p_215724_5_);
            return this.apply(context, r, r1, p_215724_4_, p_215724_5_);
         }, r, r1);
      };
   }

   int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_);
}