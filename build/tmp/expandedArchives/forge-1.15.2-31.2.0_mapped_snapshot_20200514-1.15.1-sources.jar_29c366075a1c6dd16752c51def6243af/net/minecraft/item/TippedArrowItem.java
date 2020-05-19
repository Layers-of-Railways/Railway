package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties builder) {
      super(builder);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), Potions.POISON);
   }

   /**
    * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    */
   public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
      if (this.isInGroup(group)) {
         for(Potion potion : Registry.POTION) {
            if (!potion.getEffects().isEmpty()) {
               items.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potion));
            }
         }
      }

   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
   }

   /**
    * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
    * different names based on their damage or NBT.
    */
   public String getTranslationKey(ItemStack stack) {
      return PotionUtils.getPotionFromItem(stack).getNamePrefixed(this.getTranslationKey() + ".effect.");
   }
}