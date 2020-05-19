package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class FishingRodItem extends Item {
   public FishingRodItem(Item.Properties builder) {
      super(builder);
      this.addPropertyOverride(new ResourceLocation("cast"), (p_210313_0_, p_210313_1_, p_210313_2_) -> {
         if (p_210313_2_ == null) {
            return 0.0F;
         } else {
            boolean flag = p_210313_2_.getHeldItemMainhand() == p_210313_0_;
            boolean flag1 = p_210313_2_.getHeldItemOffhand() == p_210313_0_;
            if (p_210313_2_.getHeldItemMainhand().getItem() instanceof FishingRodItem) {
               flag1 = false;
            }

            return (flag || flag1) && p_210313_2_ instanceof PlayerEntity && ((PlayerEntity)p_210313_2_).fishingBobber != null ? 1.0F : 0.0F;
         }
      });
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (playerIn.fishingBobber != null) {
         if (!worldIn.isRemote) {
            int i = playerIn.fishingBobber.handleHookRetraction(itemstack);
            itemstack.damageItem(i, playerIn, (p_220000_1_) -> {
               p_220000_1_.sendBreakAnimation(handIn);
            });
         }

         worldIn.playSound((PlayerEntity)null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      } else {
         worldIn.playSound((PlayerEntity)null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!worldIn.isRemote) {
            int k = EnchantmentHelper.getFishingSpeedBonus(itemstack);
            int j = EnchantmentHelper.getFishingLuckBonus(itemstack);
            worldIn.addEntity(new FishingBobberEntity(playerIn, worldIn, j, k));
         }

         playerIn.addStat(Stats.ITEM_USED.get(this));
      }

      return ActionResult.resultSuccess(itemstack);
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return 1;
   }
}