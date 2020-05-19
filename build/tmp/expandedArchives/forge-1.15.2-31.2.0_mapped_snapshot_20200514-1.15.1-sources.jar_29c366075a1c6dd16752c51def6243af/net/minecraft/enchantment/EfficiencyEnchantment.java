package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EfficiencyEnchantment extends Enchantment {
   protected EfficiencyEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.DIGGER, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + 10 * (enchantmentLevel - 1);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 5;
   }

   /**
    * Determines if this enchantment can be applied to a specific ItemStack.
    */
   public boolean canApply(ItemStack stack) {
      return stack.getItem() == Items.SHEARS ? true : super.canApply(stack);
   }
}