package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ThrowablePotionItem extends PotionItem {
   public ThrowablePotionItem(Item.Properties p_i225739_1_) {
      super(p_i225739_1_);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (!worldIn.isRemote) {
         PotionEntity potionentity = new PotionEntity(worldIn, playerIn);
         potionentity.setItem(itemstack);
         potionentity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
         worldIn.addEntity(potionentity);
      }

      playerIn.addStat(Stats.ITEM_USED.get(this));
      if (!playerIn.abilities.isCreativeMode) {
         itemstack.shrink(1);
      }

      return ActionResult.resultSuccess(itemstack);
   }
}