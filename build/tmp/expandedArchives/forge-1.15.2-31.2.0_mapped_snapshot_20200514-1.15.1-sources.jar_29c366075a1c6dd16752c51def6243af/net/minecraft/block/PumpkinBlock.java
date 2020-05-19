package net.minecraft.block;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class PumpkinBlock extends StemGrownBlock {
   protected PumpkinBlock(Block.Properties builder) {
      super(builder);
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      ItemStack itemstack = player.getHeldItem(handIn);
      if (itemstack.getItem() == Items.SHEARS) {
         if (!worldIn.isRemote) {
            Direction direction = hit.getFace();
            Direction direction1 = direction.getAxis() == Direction.Axis.Y ? player.getHorizontalFacing().getOpposite() : direction;
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockState(pos, Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, direction1), 11);
            ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX() + 0.5D + (double)direction1.getXOffset() * 0.65D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D + (double)direction1.getZOffset() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            itementity.setMotion(0.05D * (double)direction1.getXOffset() + worldIn.rand.nextDouble() * 0.02D, 0.05D, 0.05D * (double)direction1.getZOffset() + worldIn.rand.nextDouble() * 0.02D);
            worldIn.addEntity(itementity);
            itemstack.damageItem(1, player, (p_220282_1_) -> {
               p_220282_1_.sendBreakAnimation(handIn);
            });
         }

         return ActionResultType.SUCCESS;
      } else {
         return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
      }
   }

   public StemBlock getStem() {
      return (StemBlock)Blocks.PUMPKIN_STEM;
   }

   public AttachedStemBlock getAttachedStem() {
      return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
   }
}