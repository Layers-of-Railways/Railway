package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      PlayerEntity playerentity = context.getPlayer();
      IWorld iworld = context.getWorld();
      BlockPos blockpos = context.getPos();
      BlockState blockstate = iworld.getBlockState(blockpos);
      if (isUnlitCampfire(blockstate)) {
         iworld.playSound(playerentity, blockpos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
         iworld.setBlockState(blockpos, blockstate.with(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
         if (playerentity != null) {
            context.getItem().damageItem(1, playerentity, (p_219999_1_) -> {
               p_219999_1_.sendBreakAnimation(context.getHand());
            });
         }

         return ActionResultType.SUCCESS;
      } else {
         BlockPos blockpos1 = blockpos.offset(context.getFace());
         if (canSetFire(iworld.getBlockState(blockpos1), iworld, blockpos1)) {
            iworld.playSound(playerentity, blockpos1, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            BlockState blockstate1 = ((FireBlock)Blocks.FIRE).getStateForPlacement(iworld, blockpos1);
            iworld.setBlockState(blockpos1, blockstate1, 11);
            ItemStack itemstack = context.getItem();
            if (playerentity instanceof ServerPlayerEntity) {
               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos1, itemstack);
               itemstack.damageItem(1, playerentity, (p_219998_1_) -> {
                  p_219998_1_.sendBreakAnimation(context.getHand());
               });
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.FAIL;
         }
      }
   }

   /**
    * Checks the passed blockstate for a campfire block, if it is not waterlogged and not lit.
    */
   public static boolean isUnlitCampfire(BlockState state) {
      return state.getBlock() == Blocks.CAMPFIRE && !state.get(BlockStateProperties.WATERLOGGED) && !state.get(BlockStateProperties.LIT);
   }

   /**
    * Checks if a position is valid for fire to be set.
    */
   public static boolean canSetFire(BlockState existingState, IWorld worldIn, BlockPos posIn) {
      BlockState blockstate = ((FireBlock)Blocks.FIRE).getStateForPlacement(worldIn, posIn);
      boolean flag = false;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos framePos = posIn.offset(direction);
         if (worldIn.getBlockState(framePos).isPortalFrame(worldIn, framePos) && ((NetherPortalBlock)Blocks.NETHER_PORTAL).isPortal(worldIn, posIn) != null) {
            flag = true;
         }
      }

      return existingState.isAir() && (blockstate.isValidPosition(worldIn, posIn) || flag);
   }
}