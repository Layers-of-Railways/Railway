package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.commons.lang3.ArrayUtils;

public abstract class StandaloneLootEntry extends LootEntry {
   /** The weight of the entry. */
   protected final int weight;
   /** The quality of the entry. */
   protected final int quality;
   /** Functions that are ran on the entry. */
   protected final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> field_216157_c;
   private final ILootGenerator field_216161_h = new StandaloneLootEntry.Generator() {
      public void func_216188_a(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_) {
         StandaloneLootEntry.this.func_216154_a(ILootFunction.func_215858_a(StandaloneLootEntry.this.field_216157_c, p_216188_1_, p_216188_2_), p_216188_2_);
      }
   };

   protected StandaloneLootEntry(int weightIn, int qualityIn, ILootCondition[] conditionsIn, ILootFunction[] functionsIn) {
      super(conditionsIn);
      this.weight = weightIn;
      this.quality = qualityIn;
      this.functions = functionsIn;
      this.field_216157_c = LootFunctionManager.combine(functionsIn);
   }

   public void func_225579_a_(ValidationTracker p_225579_1_) {
      super.func_225579_a_(p_225579_1_);

      for(int i = 0; i < this.functions.length; ++i) {
         this.functions[i].func_225580_a_(p_225579_1_.func_227534_b_(".functions[" + i + "]"));
      }

   }

   protected abstract void func_216154_a(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_);

   public boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
      if (this.test(p_expand_1_)) {
         p_expand_2_.accept(this.field_216161_h);
         return true;
      } else {
         return false;
      }
   }

   public static StandaloneLootEntry.Builder<?> builder(StandaloneLootEntry.ILootEntryBuilder entryBuilderIn) {
      return new StandaloneLootEntry.BuilderImpl(entryBuilderIn);
   }

   public abstract static class Builder<T extends StandaloneLootEntry.Builder<T>> extends LootEntry.Builder<T> implements ILootFunctionConsumer<T> {
      protected int weight = 1;
      protected int quality = 0;
      private final List<ILootFunction> functions = Lists.newArrayList();

      public T acceptFunction(ILootFunction.IBuilder functionBuilder) {
         this.functions.add(functionBuilder.build());
         return (T)(this.func_212845_d_());
      }

      /**
       * Creates an array from the functions list
       */
      protected ILootFunction[] getFunctions() {
         return this.functions.toArray(new ILootFunction[0]);
      }

      public T weight(int weightIn) {
         this.weight = weightIn;
         return (T)(this.func_212845_d_());
      }

      public T quality(int qualityIn) {
         this.quality = qualityIn;
         return (T)(this.func_212845_d_());
      }
   }

   static class BuilderImpl extends StandaloneLootEntry.Builder<StandaloneLootEntry.BuilderImpl> {
      private final StandaloneLootEntry.ILootEntryBuilder field_216090_c;

      public BuilderImpl(StandaloneLootEntry.ILootEntryBuilder p_i50485_1_) {
         this.field_216090_c = p_i50485_1_;
      }

      protected StandaloneLootEntry.BuilderImpl func_212845_d_() {
         return this;
      }

      public LootEntry build() {
         return this.field_216090_c.build(this.weight, this.quality, this.func_216079_f(), this.getFunctions());
      }
   }

   public abstract class Generator implements ILootGenerator {
      /**
       * Gets the effective weight based on the loot entry's weight and quality multiplied by looter's luck.
       */
      public int getEffectiveWeight(float luck) {
         return Math.max(MathHelper.floor((float)StandaloneLootEntry.this.weight + (float)StandaloneLootEntry.this.quality * luck), 0);
      }
   }

   @FunctionalInterface
   public interface ILootEntryBuilder {
      StandaloneLootEntry build(int p_build_1_, int p_build_2_, ILootCondition[] p_build_3_, ILootFunction[] p_build_4_);
   }

   public abstract static class Serializer<T extends StandaloneLootEntry> extends LootEntry.Serializer<T> {
      public Serializer(ResourceLocation p_i50483_1_, Class<T> p_i50483_2_) {
         super(p_i50483_1_, p_i50483_2_);
      }

      public void serialize(JsonObject json, T entryIn, JsonSerializationContext context) {
         if (entryIn.weight != 1) {
            json.addProperty("weight", entryIn.weight);
         }

         if (entryIn.quality != 0) {
            json.addProperty("quality", entryIn.quality);
         }

         if (!ArrayUtils.isEmpty((Object[])entryIn.functions)) {
            json.add("functions", context.serialize(entryIn.functions));
         }

      }

      public final T deserialize(JsonObject json, JsonDeserializationContext context, ILootCondition[] conditionsIn) {
         int i = JSONUtils.getInt(json, "weight", 1);
         int j = JSONUtils.getInt(json, "quality", 0);
         ILootFunction[] ailootfunction = JSONUtils.deserializeClass(json, "functions", new ILootFunction[0], context, ILootFunction[].class);
         return (T)this.func_212829_b_(json, context, i, j, conditionsIn, ailootfunction);
      }

      protected abstract T func_212829_b_(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_);
   }
}