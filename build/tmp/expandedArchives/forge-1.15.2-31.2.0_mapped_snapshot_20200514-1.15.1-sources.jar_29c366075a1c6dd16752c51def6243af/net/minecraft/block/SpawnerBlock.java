package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SpawnerBlock extends ContainerBlock {
   protected SpawnerBlock(Block.Properties builder) {
      super(builder);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new MobSpawnerTileEntity();
   }

   /**
    * Perform side-effects from block dropping, such as creating silverfish
    */
   public void spawnAdditionalDrops(BlockState state, World worldIn, BlockPos pos, ItemStack stack) {
      super.spawnAdditionalDrops(state, worldIn, pos, stack);
   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, int fortune, int silktouch) {
      return 15 + RANDOM.nextInt(15) + RANDOM.nextInt(15);
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }
}