package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   /**
    * Returns the level of enchantment on the ItemStack passed.
    */
   public static int getEnchantmentLevel(Enchantment enchID, ItemStack stack) {
      if (stack.isEmpty()) {
         return 0;
      } else {
         ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(enchID);
         ListNBT listnbt = stack.getEnchantmentTagList();

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            ResourceLocation resourcelocation1 = ResourceLocation.tryCreate(compoundnbt.getString("id"));
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
               return MathHelper.clamp(compoundnbt.getInt("lvl"), 0, 255);
            }
         }

         return 0;
      }
   }

   /**
    * Return the enchantments for the specified stack.
    */
   public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
      ListNBT listnbt = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(stack) : stack.getEnchantmentTagList();
      return func_226652_a_(listnbt);
   }

   public static Map<Enchantment, Integer> func_226652_a_(ListNBT p_226652_0_) {
      Map<Enchantment, Integer> map = Maps.newLinkedHashMap();

      for(int i = 0; i < p_226652_0_.size(); ++i) {
         CompoundNBT compoundnbt = p_226652_0_.getCompound(i);
         Registry.ENCHANTMENT.getValue(ResourceLocation.tryCreate(compoundnbt.getString("id"))).ifPresent((p_226651_2_) -> {
            Integer integer = map.put(p_226651_2_, compoundnbt.getInt("lvl"));
         });
      }

      return map;
   }

   /**
    * Set the enchantments for the specified stack.
    */
   public static void setEnchantments(Map<Enchantment, Integer> enchMap, ItemStack stack) {
      ListNBT listnbt = new ListNBT();

      for(Entry<Enchantment, Integer> entry : enchMap.entrySet()) {
         Enchantment enchantment = entry.getKey();
         if (enchantment != null) {
            int i = entry.getValue();
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("id", String.valueOf((Object)Registry.ENCHANTMENT.getKey(enchantment)));
            compoundnbt.putShort("lvl", (short)i);
            listnbt.add(compoundnbt);
            if (stack.getItem() == Items.ENCHANTED_BOOK) {
               EnchantedBookItem.addEnchantment(stack, new EnchantmentData(enchantment, i));
            }
         }
      }

      if (listnbt.isEmpty()) {
         stack.removeChildTag("Enchantments");
      } else if (stack.getItem() != Items.ENCHANTED_BOOK) {
         stack.setTagInfo("Enchantments", listnbt);
      }

   }

   /**
    * Executes the enchantment modifier on the ItemStack passed.
    */
   private static void applyEnchantmentModifier(EnchantmentHelper.IEnchantmentVisitor modifier, ItemStack stack) {
      if (!stack.isEmpty()) {
         ListNBT listnbt = stack.getEnchantmentTagList();

         for(int i = 0; i < listnbt.size(); ++i) {
            String s = listnbt.getCompound(i).getString("id");
            int j = listnbt.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getValue(ResourceLocation.tryCreate(s)).ifPresent((p_222184_2_) -> {
               modifier.accept(p_222184_2_, j);
            });
         }

      }
   }

   /**
    * Executes the enchantment modifier on the array of ItemStack passed.
    */
   private static void applyEnchantmentModifierArray(EnchantmentHelper.IEnchantmentVisitor modifier, Iterable<ItemStack> stacks) {
      for(ItemStack itemstack : stacks) {
         applyEnchantmentModifier(modifier, itemstack);
      }

   }

   /**
    * Returns the modifier of protection enchantments on armors equipped on player.
    */
   public static int getEnchantmentModifierDamage(Iterable<ItemStack> stacks, DamageSource source) {
      MutableInt mutableint = new MutableInt();
      applyEnchantmentModifierArray((p_212576_2_, p_212576_3_) -> {
         mutableint.add(p_212576_2_.calcModifierDamage(p_212576_3_, source));
      }, stacks);
      return mutableint.intValue();
   }

   public static float getModifierForCreature(ItemStack stack, CreatureAttribute creatureAttribute) {
      MutableFloat mutablefloat = new MutableFloat();
      applyEnchantmentModifier((p_212573_2_, p_212573_3_) -> {
         mutablefloat.add(p_212573_2_.calcDamageByCreature(p_212573_3_, creatureAttribute));
      }, stack);
      return mutablefloat.floatValue();
   }

   public static float getSweepingDamageRatio(LivingEntity entityIn) {
      int i = getMaxEnchantmentLevel(Enchantments.SWEEPING, entityIn);
      return i > 0 ? SweepingEnchantment.getSweepingDamageRatio(i) : 0.0F;
   }

   public static void applyThornEnchantments(LivingEntity user, Entity attacker) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_212575_2_, p_212575_3_) -> {
         p_212575_2_.onUserHurt(user, attacker, p_212575_3_);
      };
      if (user != null) {
         applyEnchantmentModifierArray(enchantmenthelper$ienchantmentvisitor, user.getEquipmentAndArmor());
      }

      if (attacker instanceof PlayerEntity) {
         applyEnchantmentModifier(enchantmenthelper$ienchantmentvisitor, user.getHeldItemMainhand());
      }

   }

   public static void applyArthropodEnchantments(LivingEntity user, Entity target) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_212574_2_, p_212574_3_) -> {
         p_212574_2_.onEntityDamaged(user, target, p_212574_3_);
      };
      if (user != null) {
         applyEnchantmentModifierArray(enchantmenthelper$ienchantmentvisitor, user.getEquipmentAndArmor());
      }

      if (user instanceof PlayerEntity) {
         applyEnchantmentModifier(enchantmenthelper$ienchantmentvisitor, user.getHeldItemMainhand());
      }

   }

   public static int getMaxEnchantmentLevel(Enchantment enchantmentIn, LivingEntity entityIn) {
      Iterable<ItemStack> iterable = enchantmentIn.getEntityEquipment(entityIn).values();
      if (iterable == null) {
         return 0;
      } else {
         int i = 0;

         for(ItemStack itemstack : iterable) {
            int j = getEnchantmentLevel(enchantmentIn, itemstack);
            if (j > i) {
               i = j;
            }
         }

         return i;
      }
   }

   /**
    * Returns the Knockback modifier of the enchantment on the players held item.
    */
   public static int getKnockbackModifier(LivingEntity player) {
      return getMaxEnchantmentLevel(Enchantments.KNOCKBACK, player);
   }

   /**
    * Returns the fire aspect modifier of the players held item.
    */
   public static int getFireAspectModifier(LivingEntity player) {
      return getMaxEnchantmentLevel(Enchantments.FIRE_ASPECT, player);
   }

   public static int getRespirationModifier(LivingEntity entityIn) {
      return getMaxEnchantmentLevel(Enchantments.RESPIRATION, entityIn);
   }

   public static int getDepthStriderModifier(LivingEntity entityIn) {
      return getMaxEnchantmentLevel(Enchantments.DEPTH_STRIDER, entityIn);
   }

   public static int getEfficiencyModifier(LivingEntity entityIn) {
      return getMaxEnchantmentLevel(Enchantments.EFFICIENCY, entityIn);
   }

   public static int getFishingLuckBonus(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, stack);
   }

   public static int getFishingSpeedBonus(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.LURE, stack);
   }

   public static int getLootingModifier(LivingEntity entityIn) {
      return getMaxEnchantmentLevel(Enchantments.LOOTING, entityIn);
   }

   public static boolean hasAquaAffinity(LivingEntity entityIn) {
      return getMaxEnchantmentLevel(Enchantments.AQUA_AFFINITY, entityIn) > 0;
   }

   /**
    * Checks if the player has any armor enchanted with the frost walker enchantment. 
    *  @return If player has equipment with frost walker
    */
   public static boolean hasFrostWalker(LivingEntity player) {
      return getMaxEnchantmentLevel(Enchantments.FROST_WALKER, player) > 0;
   }

   public static boolean hasBindingCurse(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.BINDING_CURSE, stack) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack) > 0;
   }

   public static int getLoyaltyModifier(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.LOYALTY, stack);
   }

   public static int getRiptideModifier(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.RIPTIDE, stack);
   }

   public static boolean hasChanneling(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.CHANNELING, stack) > 0;
   }

   /**
    * Gets an item with a specified enchantment from a living entity. If there are more than one valid items a random
    * one will be returned.
    */
   @Nullable
   public static Entry<EquipmentSlotType, ItemStack> getRandomItemWithEnchantment(Enchantment targetEnchantment, LivingEntity entityIn) {
      Map<EquipmentSlotType, ItemStack> map = targetEnchantment.getEntityEquipment(entityIn);
      if (map.isEmpty()) {
         return null;
      } else {
         List<Entry<EquipmentSlotType, ItemStack>> list = Lists.newArrayList();

         for(Entry<EquipmentSlotType, ItemStack> entry : map.entrySet()) {
            ItemStack itemstack = entry.getValue();
            if (!itemstack.isEmpty() && getEnchantmentLevel(targetEnchantment, itemstack) > 0) {
               list.add(entry);
            }
         }

         return list.isEmpty() ? null : list.get(entityIn.getRNG().nextInt(list.size()));
      }
   }

   /**
    * Returns the enchantability of itemstack, using a separate calculation for each enchantNum (0, 1 or 2), cutting to
    * the max enchantability power of the table, which is locked to a max of 15.
    */
   public static int calcItemStackEnchantability(Random rand, int enchantNum, int power, ItemStack stack) {
      Item item = stack.getItem();
      int i = stack.getItemEnchantability();
      if (i <= 0) {
         return 0;
      } else {
         if (power > 15) {
            power = 15;
         }

         int j = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);
         if (enchantNum == 0) {
            return Math.max(j / 3, 1);
         } else {
            return enchantNum == 1 ? j * 2 / 3 + 1 : Math.max(j, power * 2);
         }
      }
   }

   /**
    * Applys a random enchantment to the specified item.
    */
   public static ItemStack addRandomEnchantment(Random random, ItemStack stack, int level, boolean allowTreasure) {
      List<EnchantmentData> list = buildEnchantmentList(random, stack, level, allowTreasure);
      boolean flag = stack.getItem() == Items.BOOK;
      if (flag) {
         stack = new ItemStack(Items.ENCHANTED_BOOK);
      }

      for(EnchantmentData enchantmentdata : list) {
         if (flag) {
            EnchantedBookItem.addEnchantment(stack, enchantmentdata);
         } else {
            stack.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
         }
      }

      return stack;
   }

   /**
    * Create a list of random EnchantmentData (enchantments) that can be added together to the ItemStack, the 3rd
    * parameter is the total enchantability level.
    */
   public static List<EnchantmentData> buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int level, boolean allowTreasure) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = itemStackIn.getItem();
      int i = itemStackIn.getItemEnchantability();
      if (i <= 0) {
         return list;
      } else {
         level = level + 1 + randomIn.nextInt(i / 4 + 1) + randomIn.nextInt(i / 4 + 1);
         float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
         level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
         List<EnchantmentData> list1 = getEnchantmentDatas(level, itemStackIn, allowTreasure);
         if (!list1.isEmpty()) {
            list.add(WeightedRandom.getRandomItem(randomIn, list1));

            while(randomIn.nextInt(50) <= level) {
               removeIncompatible(list1, Util.getLast(list));
               if (list1.isEmpty()) {
                  break;
               }

               list.add(WeightedRandom.getRandomItem(randomIn, list1));
               level /= 2;
            }
         }

         return list;
      }
   }

   public static void removeIncompatible(List<EnchantmentData> dataList, EnchantmentData data) {
      Iterator<EnchantmentData> iterator = dataList.iterator();

      while(iterator.hasNext()) {
         if (!data.enchantment.isCompatibleWith((iterator.next()).enchantment)) {
            iterator.remove();
         }
      }

   }

   public static boolean areAllCompatibleWith(Collection<Enchantment> enchantmentsIn, Enchantment enchantmentIn) {
      for(Enchantment enchantment : enchantmentsIn) {
         if (!enchantment.isCompatibleWith(enchantmentIn)) {
            return false;
         }
      }

      return true;
   }

   public static List<EnchantmentData> getEnchantmentDatas(int p_185291_0_, ItemStack stack, boolean allowTreasure) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = stack.getItem();
      boolean flag = stack.getItem() == Items.BOOK;

      for(Enchantment enchantment : Registry.ENCHANTMENT) {
         if ((!enchantment.isTreasureEnchantment() || allowTreasure) && (enchantment.canApplyAtEnchantingTable(stack) || (flag && enchantment.isAllowedOnBooks()))) {
            for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
               if (p_185291_0_ >= enchantment.getMinEnchantability(i) && p_185291_0_ <= enchantment.getMaxEnchantability(i)) {
                  list.add(new EnchantmentData(enchantment, i));
                  break;
               }
            }
         }
      }

      return list;
   }

   @FunctionalInterface
   interface IEnchantmentVisitor {
      void accept(Enchantment p_accept_1_, int p_accept_2_);
   }
}