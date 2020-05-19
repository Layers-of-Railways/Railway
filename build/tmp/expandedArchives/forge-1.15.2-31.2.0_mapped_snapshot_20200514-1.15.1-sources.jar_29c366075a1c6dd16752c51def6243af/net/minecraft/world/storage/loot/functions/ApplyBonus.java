package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ApplyBonus extends LootFunction {
   private static final Map<ResourceLocation, ApplyBonus.IFormulaDeserializer> field_215875_a = Maps.newHashMap();
   private final Enchantment enchantment;
   private final ApplyBonus.IFormula field_215877_d;

   private ApplyBonus(ILootCondition[] conditionsIn, Enchantment enchantmentIn, ApplyBonus.IFormula p_i51246_3_) {
      super(conditionsIn);
      this.enchantment = enchantmentIn;
      this.field_215877_d = p_i51246_3_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      ItemStack itemstack = context.get(LootParameters.TOOL);
      if (itemstack != null) {
         int i = EnchantmentHelper.getEnchantmentLevel(this.enchantment, itemstack);
         int j = this.field_215877_d.func_216204_a(context.getRandom(), stack.getCount(), i);
         stack.setCount(j);
      }

      return stack;
   }

   public static LootFunction.Builder<?> binomialWithBonusCount(Enchantment p_215870_0_, float p_215870_1_, int p_215870_2_) {
      return builder((p_215864_3_) -> {
         return new ApplyBonus(p_215864_3_, p_215870_0_, new ApplyBonus.BinomialWithBonusCountFormula(p_215870_2_, p_215870_1_));
      });
   }

   public static LootFunction.Builder<?> oreDrops(Enchantment p_215869_0_) {
      return builder((p_215866_1_) -> {
         return new ApplyBonus(p_215866_1_, p_215869_0_, new ApplyBonus.OreDropsFormula());
      });
   }

   public static LootFunction.Builder<?> uniformBonusCount(Enchantment p_215871_0_) {
      return builder((p_215872_1_) -> {
         return new ApplyBonus(p_215872_1_, p_215871_0_, new ApplyBonus.UniformBonusCountFormula(1));
      });
   }

   public static LootFunction.Builder<?> uniformBonusCount(Enchantment p_215865_0_, int p_215865_1_) {
      return builder((p_215868_2_) -> {
         return new ApplyBonus(p_215868_2_, p_215865_0_, new ApplyBonus.UniformBonusCountFormula(p_215865_1_));
      });
   }

   static {
      field_215875_a.put(ApplyBonus.BinomialWithBonusCountFormula.field_216211_a, ApplyBonus.BinomialWithBonusCountFormula::func_216210_a);
      field_215875_a.put(ApplyBonus.OreDropsFormula.field_216206_a, ApplyBonus.OreDropsFormula::func_216205_a);
      field_215875_a.put(ApplyBonus.UniformBonusCountFormula.field_216208_a, ApplyBonus.UniformBonusCountFormula::func_216207_a);
   }

   static final class BinomialWithBonusCountFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation field_216211_a = new ResourceLocation("binomial_with_bonus_count");
      private final int extra;
      private final float probability;

      public BinomialWithBonusCountFormula(int extra, float probability) {
         this.extra = extra;
         this.probability = probability;
      }

      public int func_216204_a(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         for(int i = 0; i < p_216204_3_ + this.extra; ++i) {
            if (p_216204_1_.nextFloat() < this.probability) {
               ++p_216204_2_;
            }
         }

         return p_216204_2_;
      }

      public void func_216202_a(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
         p_216202_1_.addProperty("extra", this.extra);
         p_216202_1_.addProperty("probability", this.probability);
      }

      public static ApplyBonus.IFormula func_216210_a(JsonObject p_216210_0_, JsonDeserializationContext p_216210_1_) {
         int i = JSONUtils.getInt(p_216210_0_, "extra");
         float f = JSONUtils.getFloat(p_216210_0_, "probability");
         return new ApplyBonus.BinomialWithBonusCountFormula(i, f);
      }

      public ResourceLocation func_216203_a() {
         return field_216211_a;
      }
   }

   interface IFormula {
      int func_216204_a(Random p_216204_1_, int p_216204_2_, int p_216204_3_);

      void func_216202_a(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_);

      ResourceLocation func_216203_a();
   }

   interface IFormulaDeserializer {
      ApplyBonus.IFormula deserialize(JsonObject p_deserialize_1_, JsonDeserializationContext p_deserialize_2_);
   }

   static final class OreDropsFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation field_216206_a = new ResourceLocation("ore_drops");

      private OreDropsFormula() {
      }

      public int func_216204_a(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         if (p_216204_3_ > 0) {
            int i = p_216204_1_.nextInt(p_216204_3_ + 2) - 1;
            if (i < 0) {
               i = 0;
            }

            return p_216204_2_ * (i + 1);
         } else {
            return p_216204_2_;
         }
      }

      public void func_216202_a(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
      }

      public static ApplyBonus.IFormula func_216205_a(JsonObject p_216205_0_, JsonDeserializationContext p_216205_1_) {
         return new ApplyBonus.OreDropsFormula();
      }

      public ResourceLocation func_216203_a() {
         return field_216206_a;
      }
   }

   public static class Serializer extends LootFunction.Serializer<ApplyBonus> {
      public Serializer() {
         super(new ResourceLocation("apply_bonus"), ApplyBonus.class);
      }

      public void serialize(JsonObject object, ApplyBonus functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.addProperty("enchantment", Registry.ENCHANTMENT.getKey(functionClazz.enchantment).toString());
         object.addProperty("formula", functionClazz.field_215877_d.func_216203_a().toString());
         JsonObject jsonobject = new JsonObject();
         functionClazz.field_215877_d.func_216202_a(jsonobject, serializationContext);
         if (jsonobject.size() > 0) {
            object.add("parameters", jsonobject);
         }

      }

      public ApplyBonus deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(object, "enchantment"));
         Enchantment enchantment = Registry.ENCHANTMENT.getValue(resourcelocation).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + resourcelocation);
         });
         ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(object, "formula"));
         ApplyBonus.IFormulaDeserializer applybonus$iformuladeserializer = ApplyBonus.field_215875_a.get(resourcelocation1);
         if (applybonus$iformuladeserializer == null) {
            throw new JsonParseException("Invalid formula id: " + resourcelocation1);
         } else {
            ApplyBonus.IFormula applybonus$iformula;
            if (object.has("parameters")) {
               applybonus$iformula = applybonus$iformuladeserializer.deserialize(JSONUtils.getJsonObject(object, "parameters"), deserializationContext);
            } else {
               applybonus$iformula = applybonus$iformuladeserializer.deserialize(new JsonObject(), deserializationContext);
            }

            return new ApplyBonus(conditionsIn, enchantment, applybonus$iformula);
         }
      }
   }

   static final class UniformBonusCountFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation field_216208_a = new ResourceLocation("uniform_bonus_count");
      private final int bonusMultiplier;

      public UniformBonusCountFormula(int bonusMultiplier) {
         this.bonusMultiplier = bonusMultiplier;
      }

      public int func_216204_a(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         return p_216204_2_ + p_216204_1_.nextInt(this.bonusMultiplier * p_216204_3_ + 1);
      }

      public void func_216202_a(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
         p_216202_1_.addProperty("bonusMultiplier", this.bonusMultiplier);
      }

      public static ApplyBonus.IFormula func_216207_a(JsonObject p_216207_0_, JsonDeserializationContext p_216207_1_) {
         int i = JSONUtils.getInt(p_216207_0_, "bonusMultiplier");
         return new ApplyBonus.UniformBonusCountFormula(i);
      }

      public ResourceLocation func_216203_a() {
         return field_216208_a;
      }
   }
}