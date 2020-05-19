package net.minecraft.block;

import net.minecraft.item.DyeColor;

public class StainedGlassPaneBlock extends PaneBlock implements IBeaconBeamColorProvider {
   private final DyeColor color;

   public StainedGlassPaneBlock(DyeColor colorIn, Block.Properties properties) {
      super(properties);
      this.color = colorIn;
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public DyeColor getColor() {
      return this.color;
   }
}