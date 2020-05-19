package net.minecraft.block;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GravelBlock extends FallingBlock {
   public GravelBlock(Block.Properties properties) {
      super(properties);
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState state) {
      return -8356741;
   }
}