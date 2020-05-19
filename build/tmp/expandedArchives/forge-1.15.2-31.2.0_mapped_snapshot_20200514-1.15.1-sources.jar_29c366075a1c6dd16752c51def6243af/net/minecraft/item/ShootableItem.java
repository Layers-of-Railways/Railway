package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;

public abstract class ShootableItem extends Item {
   public static final Predicate<ItemStack> ARROWS = (p_220002_0_) -> {
      return p_220002_0_.getItem().isIn(ItemTags.ARROWS);
   };
   public static final Predicate<ItemStack> ARROWS_OR_FIREWORKS = ARROWS.or((p_220003_0_) -> {
      return p_220003_0_.getItem() == Items.FIREWORK_ROCKET;
   });

   public ShootableItem(Item.Properties p_i50040_1_) {
      super(p_i50040_1_);
   }

   public Predicate<ItemStack> getAmmoPredicate() {
      return this.getInventoryAmmoPredicate();
   }

   /**
    * Get the predicate to match ammunition when searching the player's inventory, not their main/offhand
    */
   public abstract Predicate<ItemStack> getInventoryAmmoPredicate();

   public static ItemStack getHeldAmmo(LivingEntity living, Predicate<ItemStack> isAmmo) {
      if (isAmmo.test(living.getHeldItem(Hand.OFF_HAND))) {
         return living.getHeldItem(Hand.OFF_HAND);
      } else {
         return isAmmo.test(living.getHeldItem(Hand.MAIN_HAND)) ? living.getHeldItem(Hand.MAIN_HAND) : ItemStack.EMPTY;
      }
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return 1;
   }
}