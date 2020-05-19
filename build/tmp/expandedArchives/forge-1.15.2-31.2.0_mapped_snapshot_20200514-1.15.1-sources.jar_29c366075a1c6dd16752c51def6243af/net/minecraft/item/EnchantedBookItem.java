package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnchantedBookItem extends Item {
   public EnchantedBookItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   /**
    * Checks isDamagable and if it cannot be stacked
    */
   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   public static ListNBT getEnchantments(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getTag();
      return compoundnbt != null ? compoundnbt.getList("StoredEnchantments", 10) : new ListNBT();
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      ItemStack.addEnchantmentTooltips(tooltip, getEnchantments(stack));
   }

   /**
    * Adds an stored enchantment to an enchanted book ItemStack
    */
   public static void addEnchantment(ItemStack p_92115_0_, EnchantmentData stack) {
      ListNBT listnbt = getEnchantments(p_92115_0_);
      boolean flag = true;
      ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(stack.enchantment);

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         ResourceLocation resourcelocation1 = ResourceLocation.tryCreate(compoundnbt.getString("id"));
         if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
            if (compoundnbt.getInt("lvl") < stack.enchantmentLevel) {
               compoundnbt.putShort("lvl", (short)stack.enchantmentLevel);
            }

            flag = false;
            break;
         }
      }

      if (flag) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         compoundnbt1.putString("id", String.valueOf((Object)resourcelocation));
         compoundnbt1.putShort("lvl", (short)stack.enchantmentLevel);
         listnbt.add(compoundnbt1);
      }

      p_92115_0_.getOrCreateTag().put("StoredEnchantments", listnbt);
   }

   /**
    * Returns the ItemStack of an enchanted version of this item.
    */
   public static ItemStack getEnchantedItemStack(EnchantmentData p_92111_0_) {
      ItemStack itemstack = new ItemStack(Items.ENCHANTED_BOOK);
      addEnchantment(itemstack, p_92111_0_);
      return itemstack;
   }

   /**
    * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    */
   public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
      if (group == ItemGroup.SEARCH) {
         for(Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.type != null) {
               for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
                  items.add(getEnchantedItemStack(new EnchantmentData(enchantment, i)));
               }
            }
         }
      } else if (group.getRelevantEnchantmentTypes().length != 0) {
         for(Enchantment enchantment1 : Registry.ENCHANTMENT) {
            if (group.hasRelevantEnchantmentType(enchantment1.type)) {
               items.add(getEnchantedItemStack(new EnchantmentData(enchantment1, enchantment1.getMaxLevel())));
            }
         }
      }

   }
}