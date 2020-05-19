package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class DepthStriderEnchantment extends Enchantment {
   public DepthStriderEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.ARMOR_FEET, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return enchantmentLevel * 10;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 15;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 3;
   }

   /**
    * Determines if the enchantment passed can be applyied together with this enchantment.
    */
   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.FROST_WALKER;
   }
}