package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class MendingEnchantment extends Enchantment {
   public MendingEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.BREAKABLE, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return enchantmentLevel * 25;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 50;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 1;
   }
}