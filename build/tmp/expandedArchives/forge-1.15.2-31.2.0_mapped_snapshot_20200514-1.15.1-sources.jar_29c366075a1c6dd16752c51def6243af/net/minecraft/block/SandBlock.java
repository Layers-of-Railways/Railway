package net.minecraft.block;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SandBlock extends FallingBlock {
   private final int dustColor;

   public SandBlock(int dustColorIn, Block.Properties properties) {
      super(properties);
      this.dustColor = dustColorIn;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState state) {
      return this.dustColor;
   }
}