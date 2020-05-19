package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class EggItem extends Item {
   public EggItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      worldIn.playSound((PlayerEntity)null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isRemote) {
         EggEntity eggentity = new EggEntity(worldIn, playerIn);
         eggentity.setItem(itemstack);
         eggentity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
         worldIn.addEntity(eggentity);
      }

      playerIn.addStat(Stats.ITEM_USED.get(this));
      if (!playerIn.abilities.isCreativeMode) {
         itemstack.shrink(1);
      }

      return ActionResult.resultSuccess(itemstack);
   }
}