package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class FlameEnchantment extends Enchantment {
   public FlameEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.BOW, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 20;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 1;
   }
}