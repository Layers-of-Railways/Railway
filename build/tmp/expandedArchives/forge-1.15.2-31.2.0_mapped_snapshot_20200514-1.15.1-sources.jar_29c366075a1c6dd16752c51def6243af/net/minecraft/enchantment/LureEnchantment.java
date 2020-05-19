package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class LureEnchantment extends Enchantment {
   protected LureEnchantment(Enchantment.Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
      super(rarityIn, typeIn, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 15 + (enchantmentLevel - 1) * 9;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 3;
   }
}