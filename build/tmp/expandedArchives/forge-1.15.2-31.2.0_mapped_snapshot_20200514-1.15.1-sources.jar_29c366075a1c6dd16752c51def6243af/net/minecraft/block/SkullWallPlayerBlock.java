package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;

public class SkullWallPlayerBlock extends WallSkullBlock {
   protected SkullWallPlayerBlock(Block.Properties properties) {
      super(SkullBlock.Types.PLAYER, properties);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      Blocks.PLAYER_HEAD.onBlockPlacedBy(worldIn, pos, state, placer, stack);
   }

   public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
      return Blocks.PLAYER_HEAD.getDrops(state, builder);
   }
}