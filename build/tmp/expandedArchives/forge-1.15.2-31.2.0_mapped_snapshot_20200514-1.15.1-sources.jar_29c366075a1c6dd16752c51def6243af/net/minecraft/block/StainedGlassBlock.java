package net.minecraft.block;

import net.minecraft.item.DyeColor;

public class StainedGlassBlock extends AbstractGlassBlock implements IBeaconBeamColorProvider {
   private final DyeColor color;

   public StainedGlassBlock(DyeColor colorIn, Block.Properties properties) {
      super(properties);
      this.color = colorIn;
   }

   public DyeColor getColor() {
      return this.color;
   }
}