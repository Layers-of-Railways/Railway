package net.minecraft.item;

public class BookItem extends Item {
   public BookItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Checks isDamagable and if it cannot be stacked
    */
   public boolean isEnchantable(ItemStack stack) {
      return stack.getCount() == 1;
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return 1;
   }
}