package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionUtils {
   /**
    * Creates a List of PotionEffect from data on the passed ItemStack's NBTTagCompound.
    */
   public static List<EffectInstance> getEffectsFromStack(ItemStack stack) {
      return getEffectsFromTag(stack.getTag());
   }

   public static List<EffectInstance> mergeEffects(Potion potionIn, Collection<EffectInstance> effects) {
      List<EffectInstance> list = Lists.newArrayList();
      list.addAll(potionIn.getEffects());
      list.addAll(effects);
      return list;
   }

   /**
    * Creates a list of PotionEffect from data on a NBTTagCompound.
    */
   public static List<EffectInstance> getEffectsFromTag(@Nullable CompoundNBT tag) {
      List<EffectInstance> list = Lists.newArrayList();
      list.addAll(getPotionTypeFromNBT(tag).getEffects());
      addCustomPotionEffectToList(tag, list);
      return list;
   }

   public static List<EffectInstance> getFullEffectsFromItem(ItemStack itemIn) {
      return getFullEffectsFromTag(itemIn.getTag());
   }

   public static List<EffectInstance> getFullEffectsFromTag(@Nullable CompoundNBT tag) {
      List<EffectInstance> list = Lists.newArrayList();
      addCustomPotionEffectToList(tag, list);
      return list;
   }

   public static void addCustomPotionEffectToList(@Nullable CompoundNBT tag, List<EffectInstance> effectList) {
      if (tag != null && tag.contains("CustomPotionEffects", 9)) {
         ListNBT listnbt = tag.getList("CustomPotionEffects", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            EffectInstance effectinstance = EffectInstance.read(compoundnbt);
            if (effectinstance != null) {
               effectList.add(effectinstance);
            }
         }
      }

   }

   public static int getColor(ItemStack itemStackIn) {
      CompoundNBT compoundnbt = itemStackIn.getTag();
      if (compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99)) {
         return compoundnbt.getInt("CustomPotionColor");
      } else {
         return getPotionFromItem(itemStackIn) == Potions.EMPTY ? 16253176 : getPotionColorFromEffectList(getEffectsFromStack(itemStackIn));
      }
   }

   public static int getPotionColor(Potion potionIn) {
      return potionIn == Potions.EMPTY ? 16253176 : getPotionColorFromEffectList(potionIn.getEffects());
   }

   public static int getPotionColorFromEffectList(Collection<EffectInstance> effects) {
      int i = 3694022;
      if (effects.isEmpty()) {
         return 3694022;
      } else {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         int j = 0;

         for(EffectInstance effectinstance : effects) {
            if (effectinstance.doesShowParticles()) {
               int k = effectinstance.getPotion().getLiquidColor();
               int l = effectinstance.getAmplifier() + 1;
               f += (float)(l * (k >> 16 & 255)) / 255.0F;
               f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
               f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
               j += l;
            }
         }

         if (j == 0) {
            return 0;
         } else {
            f = f / (float)j * 255.0F;
            f1 = f1 / (float)j * 255.0F;
            f2 = f2 / (float)j * 255.0F;
            return (int)f << 16 | (int)f1 << 8 | (int)f2;
         }
      }
   }

   public static Potion getPotionFromItem(ItemStack itemIn) {
      return getPotionTypeFromNBT(itemIn.getTag());
   }

   /**
    * If no correct potion is found, returns the default one : PotionTypes.water
    */
   public static Potion getPotionTypeFromNBT(@Nullable CompoundNBT tag) {
      return tag == null ? Potions.EMPTY : Potion.getPotionTypeForName(tag.getString("Potion"));
   }

   public static ItemStack addPotionToItemStack(ItemStack itemIn, Potion potionIn) {
      ResourceLocation resourcelocation = Registry.POTION.getKey(potionIn);
      if (potionIn == Potions.EMPTY) {
         itemIn.removeChildTag("Potion");
      } else {
         itemIn.getOrCreateTag().putString("Potion", resourcelocation.toString());
      }

      return itemIn;
   }

   public static ItemStack appendEffects(ItemStack itemIn, Collection<EffectInstance> effects) {
      if (effects.isEmpty()) {
         return itemIn;
      } else {
         CompoundNBT compoundnbt = itemIn.getOrCreateTag();
         ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", 9);

         for(EffectInstance effectinstance : effects) {
            listnbt.add(effectinstance.write(new CompoundNBT()));
         }

         compoundnbt.put("CustomPotionEffects", listnbt);
         return itemIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void addPotionTooltip(ItemStack itemIn, List<ITextComponent> lores, float durationFactor) {
      List<EffectInstance> list = getEffectsFromStack(itemIn);
      List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
      if (list.isEmpty()) {
         lores.add((new TranslationTextComponent("effect.none")).applyTextStyle(TextFormatting.GRAY));
      } else {
         for(EffectInstance effectinstance : list) {
            ITextComponent itextcomponent = new TranslationTextComponent(effectinstance.getEffectName());
            Effect effect = effectinstance.getPotion();
            Map<IAttribute, AttributeModifier> map = effect.getAttributeModifierMap();
            if (!map.isEmpty()) {
               for(Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                  AttributeModifier attributemodifier = entry.getValue();
                  AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierAmount(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                  list1.add(new Tuple<>(entry.getKey().getName(), attributemodifier1));
               }
            }

            if (effectinstance.getAmplifier() > 0) {
               itextcomponent.appendText(" ").appendSibling(new TranslationTextComponent("potion.potency." + effectinstance.getAmplifier()));
            }

            if (effectinstance.getDuration() > 20) {
               itextcomponent.appendText(" (").appendText(EffectUtils.getPotionDurationString(effectinstance, durationFactor)).appendText(")");
            }

            lores.add(itextcomponent.applyTextStyle(effect.getEffectType().getColor()));
         }
      }

      if (!list1.isEmpty()) {
         lores.add(new StringTextComponent(""));
         lores.add((new TranslationTextComponent("potion.whenDrank")).applyTextStyle(TextFormatting.DARK_PURPLE));

         for(Tuple<String, AttributeModifier> tuple : list1) {
            AttributeModifier attributemodifier2 = tuple.getB();
            double d0 = attributemodifier2.getAmount();
            double d1;
            if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
               d1 = attributemodifier2.getAmount();
            } else {
               d1 = attributemodifier2.getAmount() * 100.0D;
            }

            if (d0 > 0.0D) {
               lores.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent("attribute.name." + (String)tuple.getA()))).applyTextStyle(TextFormatting.BLUE));
            } else if (d0 < 0.0D) {
               d1 = d1 * -1.0D;
               lores.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent("attribute.name." + (String)tuple.getA()))).applyTextStyle(TextFormatting.RED));
            }
         }
      }

   }
}