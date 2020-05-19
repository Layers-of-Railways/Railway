package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class VanishingCurseEnchantment extends Enchantment {
   public VanishingCurseEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.ALL, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 25;
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

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean isCurse() {
      return true;
   }
}