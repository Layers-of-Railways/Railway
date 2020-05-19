package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BarrierBlock extends Block {
   protected BarrierBlock(Block.Properties properties) {
      super(properties);
   }

   public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
      return true;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.INVISIBLE;
   }

   @OnlyIn(Dist.CLIENT)
   public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return 1.0F;
   }

   public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
      return false;
   }
}