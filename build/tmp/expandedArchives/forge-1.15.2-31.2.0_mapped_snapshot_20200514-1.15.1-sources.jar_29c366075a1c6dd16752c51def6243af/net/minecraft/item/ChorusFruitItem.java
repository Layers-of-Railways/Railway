package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChorusFruitItem extends Item {
   public ChorusFruitItem(Item.Properties p_i50053_1_) {
      super(p_i50053_1_);
   }

   /**
    * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
    * the Item before the action is complete.
    */
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
      ItemStack itemstack = super.onItemUseFinish(stack, worldIn, entityLiving);
      if (!worldIn.isRemote) {
         double d0 = entityLiving.getPosX();
         double d1 = entityLiving.getPosY();
         double d2 = entityLiving.getPosZ();

         for(int i = 0; i < 16; ++i) {
            double d3 = entityLiving.getPosX() + (entityLiving.getRNG().nextDouble() - 0.5D) * 16.0D;
            double d4 = MathHelper.clamp(entityLiving.getPosY() + (double)(entityLiving.getRNG().nextInt(16) - 8), 0.0D, (double)(worldIn.getActualHeight() - 1));
            double d5 = entityLiving.getPosZ() + (entityLiving.getRNG().nextDouble() - 0.5D) * 16.0D;
            if (entityLiving.isPassenger()) {
               entityLiving.stopRiding();
            }

            if (entityLiving.attemptTeleport(d3, d4, d5, true)) {
               worldIn.playSound((PlayerEntity)null, d0, d1, d2, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
               entityLiving.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
               break;
            }
         }

         if (entityLiving instanceof PlayerEntity) {
            ((PlayerEntity)entityLiving).getCooldownTracker().setCooldown(this, 20);
         }
      }

      return itemstack;
   }
}