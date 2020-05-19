package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SnowballItem extends Item {
   public SnowballItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      worldIn.playSound((PlayerEntity)null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isRemote) {
         SnowballEntity snowballentity = new SnowballEntity(worldIn, playerIn);
         snowballentity.setItem(itemstack);
         snowballentity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
         worldIn.addEntity(snowballentity);
      }

      playerIn.addStat(Stats.ITEM_USED.get(this));
      if (!playerIn.abilities.isCreativeMode) {
         itemstack.shrink(1);
      }

      return ActionResult.resultSuccess(itemstack);
   }
}