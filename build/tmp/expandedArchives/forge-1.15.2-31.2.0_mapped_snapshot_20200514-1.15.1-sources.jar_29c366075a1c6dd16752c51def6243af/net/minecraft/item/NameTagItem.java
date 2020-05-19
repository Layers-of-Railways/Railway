package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class NameTagItem extends Item {
   public NameTagItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Returns true if the item can be used on the given entity, e.g. shears on sheep.
    */
   public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
      if (stack.hasDisplayName() && !(target instanceof PlayerEntity)) {
         if (target.isAlive()) {
            target.setCustomName(stack.getDisplayName());
            if (target instanceof MobEntity) {
               ((MobEntity)target).enablePersistence();
            }

            stack.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }
}