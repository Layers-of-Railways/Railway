package net.minecraft.world.gen.layer.traits;

public interface IDimOffset1Transformer extends IDimTransformer {
   default int func_215721_a(int p_215721_1_) {
      return p_215721_1_ - 1;
   }

   default int func_215722_b(int p_215722_1_) {
      return p_215722_1_ - 1;
   }
}