package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Returns true if the item can be used on the given entity, e.g. shears on sheep.
    */
   public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
      if (target instanceof PigEntity) {
         PigEntity pigentity = (PigEntity)target;
         if (pigentity.isAlive() && !pigentity.getSaddled() && !pigentity.isChild()) {
            pigentity.setSaddled(true);
            pigentity.world.playSound(playerIn, pigentity.getPosX(), pigentity.getPosY(), pigentity.getPosZ(), SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
            stack.shrink(1);
            return true;
         }
      }

      return false;
   }
}