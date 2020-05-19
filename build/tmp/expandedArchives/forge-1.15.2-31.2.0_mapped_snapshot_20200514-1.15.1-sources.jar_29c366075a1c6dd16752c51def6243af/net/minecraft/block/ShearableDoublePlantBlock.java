package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;

public class ShearableDoublePlantBlock extends DoublePlantBlock implements net.minecraftforge.common.IShearable {
   public static final EnumProperty<DoubleBlockHalf> PLANT_HALF = DoublePlantBlock.HALF;

   public ShearableDoublePlantBlock(Block.Properties propertiesIn) {
      super(propertiesIn);
   }

   public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
      boolean flag = super.isReplaceable(state, useContext);
      return flag && useContext.getItem().getItem() == this.asItem() ? false : flag;
   }
}