package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyValue;

public enum ItemTier implements IItemTier {
   WOOD(0, 59, 2.0F, 0.0F, 15, () -> {
      return Ingredient.fromTag(ItemTags.PLANKS);
   }),
   STONE(1, 131, 4.0F, 1.0F, 5, () -> {
      return Ingredient.fromItems(Blocks.COBBLESTONE);
   }),
   IRON(2, 250, 6.0F, 2.0F, 14, () -> {
      return Ingredient.fromItems(Items.IRON_INGOT);
   }),
   DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
      return Ingredient.fromItems(Items.DIAMOND);
   }),
   GOLD(0, 32, 12.0F, 0.0F, 22, () -> {
      return Ingredient.fromItems(Items.GOLD_INGOT);
   });

   private final int harvestLevel;
   private final int maxUses;
   private final float efficiency;
   private final float attackDamage;
   private final int enchantability;
   private final LazyValue<Ingredient> repairMaterial;

   private ItemTier(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
      this.harvestLevel = harvestLevelIn;
      this.maxUses = maxUsesIn;
      this.efficiency = efficiencyIn;
      this.attackDamage = attackDamageIn;
      this.enchantability = enchantabilityIn;
      this.repairMaterial = new LazyValue<>(repairMaterialIn);
   }

   public int getMaxUses() {
      return this.maxUses;
   }

   public float getEfficiency() {
      return this.efficiency;
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public int getHarvestLevel() {
      return this.harvestLevel;
   }

   public int getEnchantability() {
      return this.enchantability;
   }

   public Ingredient getRepairMaterial() {
      return this.repairMaterial.getValue();
   }
}