package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WitherSkeletonWallSkullBlock extends WallSkullBlock {
   protected WitherSkeletonWallSkullBlock(Block.Properties properties) {
      super(SkullBlock.Types.WITHER_SKELETON, properties);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      Blocks.WITHER_SKELETON_SKULL.onBlockPlacedBy(worldIn, pos, state, placer, stack);
   }
}