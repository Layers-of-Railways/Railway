package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class MultishotEnchantment extends Enchantment {
   public MultishotEnchantment(Enchantment.Rarity p_i50017_1_, EquipmentSlotType... p_i50017_2_) {
      super(p_i50017_1_, EnchantmentType.CROSSBOW, p_i50017_2_);
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

   /**
    * Determines if the enchantment passed can be applyied together with this enchantment.
    */
   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.PIERCING;
   }
}