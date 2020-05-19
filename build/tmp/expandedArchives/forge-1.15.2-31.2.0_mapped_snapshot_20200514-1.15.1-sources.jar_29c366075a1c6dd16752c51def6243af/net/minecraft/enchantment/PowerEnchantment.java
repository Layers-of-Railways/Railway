package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class PowerEnchantment extends Enchantment {
   public PowerEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.BOW, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + (enchantmentLevel - 1) * 10;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 15;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 5;
   }
}