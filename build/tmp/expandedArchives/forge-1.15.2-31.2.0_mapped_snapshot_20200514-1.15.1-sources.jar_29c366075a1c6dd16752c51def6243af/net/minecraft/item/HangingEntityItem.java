package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HangingEntityItem extends Item {
   private final EntityType<? extends HangingEntity> hangingEntity;

   public HangingEntityItem(EntityType<? extends HangingEntity> entityTypeIn, Item.Properties properties) {
      super(properties);
      this.hangingEntity = entityTypeIn;
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      BlockPos blockpos = context.getPos();
      Direction direction = context.getFace();
      BlockPos blockpos1 = blockpos.offset(direction);
      PlayerEntity playerentity = context.getPlayer();
      ItemStack itemstack = context.getItem();
      if (playerentity != null && !this.canPlace(playerentity, direction, itemstack, blockpos1)) {
         return ActionResultType.FAIL;
      } else {
         World world = context.getWorld();
         HangingEntity hangingentity;
         if (this.hangingEntity == EntityType.PAINTING) {
            hangingentity = new PaintingEntity(world, blockpos1, direction);
         } else {
            if (this.hangingEntity != EntityType.ITEM_FRAME) {
               return ActionResultType.SUCCESS;
            }

            hangingentity = new ItemFrameEntity(world, blockpos1, direction);
         }

         CompoundNBT compoundnbt = itemstack.getTag();
         if (compoundnbt != null) {
            EntityType.applyItemNBT(world, playerentity, hangingentity, compoundnbt);
         }

         if (hangingentity.onValidSurface()) {
            if (!world.isRemote) {
               hangingentity.playPlaceSound();
               world.addEntity(hangingentity);
            }

            itemstack.shrink(1);
            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.CONSUME;
         }
      }
   }

   protected boolean canPlace(PlayerEntity playerIn, Direction directionIn, ItemStack itemStackIn, BlockPos posIn) {
      return !directionIn.getAxis().isVertical() && playerIn.canPlayerEdit(posIn, directionIn, itemStackIn);
   }
}