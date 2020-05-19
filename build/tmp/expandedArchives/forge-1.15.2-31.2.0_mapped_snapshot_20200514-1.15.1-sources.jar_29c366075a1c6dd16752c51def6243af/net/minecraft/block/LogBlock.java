package net.minecraft.block;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LogBlock extends RotatedPillarBlock {
   private final MaterialColor verticalColor;

   public LogBlock(MaterialColor verticalColorIn, Block.Properties properties) {
      super(properties);
      this.verticalColor = verticalColorIn;
   }

   /**
    * Get the MapColor for this Block and the given BlockState
    * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public MaterialColor getMaterialColor(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(AXIS) == Direction.Axis.Y ? this.verticalColor : this.materialColor;
   }
}