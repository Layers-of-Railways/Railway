package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HayBlock extends RotatedPillarBlock {
   public HayBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Direction.Axis.Y));
   }

   /**
    * Block's chance to react to a living entity falling on it.
    */
   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      entityIn.onLivingFall(fallDistance, 0.2F);
   }
}