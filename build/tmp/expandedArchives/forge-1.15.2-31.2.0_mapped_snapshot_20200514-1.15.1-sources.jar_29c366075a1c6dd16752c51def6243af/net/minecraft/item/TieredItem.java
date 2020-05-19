package net.minecraft.item;

public class TieredItem extends Item {
   private final IItemTier tier;

   public TieredItem(IItemTier tierIn, Item.Properties builder) {
      super(builder.defaultMaxDamage(tierIn.getMaxUses()));
      this.tier = tierIn;
   }

   public IItemTier getTier() {
      return this.tier;
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return this.tier.getEnchantability();
   }

   /**
    * Return whether this item is repairable in an anvil.
    */
   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return this.tier.getRepairMaterial().test(repair) || super.getIsRepairable(toRepair, repair);
   }
}