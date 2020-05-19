package net.minecraft.block;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BeaconBlock extends ContainerBlock implements IBeaconBeamColorProvider {
   public BeaconBlock(Block.Properties properties) {
      super(properties);
   }

   public DyeColor getColor() {
      return DyeColor.WHITE;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new BeaconTileEntity();
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof BeaconTileEntity) {
            player.openContainer((BeaconTileEntity)tileentity);
            player.addStat(Stats.INTERACT_WITH_BEACON);
         }

         return ActionResultType.SUCCESS;
      }
   }

   public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return false;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof BeaconTileEntity) {
            ((BeaconTileEntity)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }
}