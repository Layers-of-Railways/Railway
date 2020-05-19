package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class IceBlock extends BreakableBlock {
   public IceBlock(Block.Properties properties) {
      super(properties);
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
         if (worldIn.dimension.doesWaterVaporize()) {
            worldIn.removeBlock(pos, false);
            return;
         }

         Material material = worldIn.getBlockState(pos.down()).getMaterial();
         if (material.blocksMovement() || material.isLiquid()) {
            worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
         }
      }

   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (worldIn.getLightFor(LightType.BLOCK, pos) > 11 - state.getOpacity(worldIn, pos)) {
         this.turnIntoWater(state, worldIn, pos);
      }

   }

   protected void turnIntoWater(BlockState p_196454_1_, World p_196454_2_, BlockPos p_196454_3_) {
      if (p_196454_2_.dimension.doesWaterVaporize()) {
         p_196454_2_.removeBlock(p_196454_3_, false);
      } else {
         p_196454_2_.setBlockState(p_196454_3_, Blocks.WATER.getDefaultState());
         p_196454_2_.neighborChanged(p_196454_3_, Blocks.WATER, p_196454_3_);
      }
   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public PushReaction getPushReaction(BlockState state) {
      return PushReaction.NORMAL;
   }

   public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
      return type == EntityType.POLAR_BEAR;
   }
}