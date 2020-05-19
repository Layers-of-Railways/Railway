package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LeadItem extends Item {
   public LeadItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      Block block = world.getBlockState(blockpos).getBlock();
      if (block.isIn(BlockTags.FENCES)) {
         PlayerEntity playerentity = context.getPlayer();
         if (!world.isRemote && playerentity != null) {
            func_226641_a_(playerentity, world, blockpos);
         }

         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public static ActionResultType func_226641_a_(PlayerEntity p_226641_0_, World p_226641_1_, BlockPos p_226641_2_) {
      LeashKnotEntity leashknotentity = null;
      boolean flag = false;
      double d0 = 7.0D;
      int i = p_226641_2_.getX();
      int j = p_226641_2_.getY();
      int k = p_226641_2_.getZ();

      for(MobEntity mobentity : p_226641_1_.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB((double)i - 7.0D, (double)j - 7.0D, (double)k - 7.0D, (double)i + 7.0D, (double)j + 7.0D, (double)k + 7.0D))) {
         if (mobentity.getLeashHolder() == p_226641_0_) {
            if (leashknotentity == null) {
               leashknotentity = LeashKnotEntity.create(p_226641_1_, p_226641_2_);
            }

            mobentity.setLeashHolder(leashknotentity, true);
            flag = true;
         }
      }

      return flag ? ActionResultType.SUCCESS : ActionResultType.PASS;
   }
}