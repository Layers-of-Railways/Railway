package net.minecraft.world.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ValidationTracker {
   private final Multimap<String, String> field_227519_a_;
   private final Supplier<String> field_227520_b_;
   private final LootParameterSet field_227521_c_;
   private final Function<ResourceLocation, ILootCondition> field_227522_d_;
   private final Set<ResourceLocation> field_227523_e_;
   private final Function<ResourceLocation, LootTable> field_227524_f_;
   private final Set<ResourceLocation> field_227525_g_;
   private String field_227526_h_;

   public ValidationTracker(LootParameterSet p_i225889_1_, Function<ResourceLocation, ILootCondition> p_i225889_2_, Function<ResourceLocation, LootTable> p_i225889_3_) {
      this(HashMultimap.create(), () -> {
         return "";
      }, p_i225889_1_, p_i225889_2_, ImmutableSet.of(), p_i225889_3_, ImmutableSet.of());
   }

   public ValidationTracker(Multimap<String, String> p_i225888_1_, Supplier<String> p_i225888_2_, LootParameterSet p_i225888_3_, Function<ResourceLocation, ILootCondition> p_i225888_4_, Set<ResourceLocation> p_i225888_5_, Function<ResourceLocation, LootTable> p_i225888_6_, Set<ResourceLocation> p_i225888_7_) {
      this.field_227519_a_ = p_i225888_1_;
      this.field_227520_b_ = p_i225888_2_;
      this.field_227521_c_ = p_i225888_3_;
      this.field_227522_d_ = p_i225888_4_;
      this.field_227523_e_ = p_i225888_5_;
      this.field_227524_f_ = p_i225888_6_;
      this.field_227525_g_ = p_i225888_7_;
   }

   private String func_227533_b_() {
      if (this.field_227526_h_ == null) {
         this.field_227526_h_ = this.field_227520_b_.get();
      }

      return this.field_227526_h_;
   }

   public void addProblem(String p_227530_1_) {
      this.field_227519_a_.put(this.func_227533_b_(), p_227530_1_);
   }

   public ValidationTracker func_227534_b_(String p_227534_1_) {
      return new ValidationTracker(this.field_227519_a_, () -> {
         return this.func_227533_b_() + p_227534_1_;
      }, this.field_227521_c_, this.field_227522_d_, this.field_227523_e_, this.field_227524_f_, this.field_227525_g_);
   }

   public ValidationTracker func_227531_a_(String p_227531_1_, ResourceLocation p_227531_2_) {
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.<ResourceLocation>builder().addAll(this.field_227525_g_).add(p_227531_2_).build();
      return new ValidationTracker(this.field_227519_a_, () -> {
         return this.func_227533_b_() + p_227531_1_;
      }, this.field_227521_c_, this.field_227522_d_, this.field_227523_e_, this.field_227524_f_, immutableset);
   }

   public ValidationTracker func_227535_b_(String p_227535_1_, ResourceLocation p_227535_2_) {
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.<ResourceLocation>builder().addAll(this.field_227523_e_).add(p_227535_2_).build();
      return new ValidationTracker(this.field_227519_a_, () -> {
         return this.func_227533_b_() + p_227535_1_;
      }, this.field_227521_c_, this.field_227522_d_, immutableset, this.field_227524_f_, this.field_227525_g_);
   }

   public boolean func_227532_a_(ResourceLocation p_227532_1_) {
      return this.field_227525_g_.contains(p_227532_1_);
   }

   public boolean func_227536_b_(ResourceLocation p_227536_1_) {
      return this.field_227523_e_.contains(p_227536_1_);
   }

   public Multimap<String, String> getProblems() {
      return ImmutableMultimap.copyOf(this.field_227519_a_);
   }

   public void func_227528_a_(IParameterized p_227528_1_) {
      this.field_227521_c_.func_227556_a_(this, p_227528_1_);
   }

   @Nullable
   public LootTable func_227539_c_(ResourceLocation p_227539_1_) {
      return this.field_227524_f_.apply(p_227539_1_);
   }

   @Nullable
   public ILootCondition func_227541_d_(ResourceLocation p_227541_1_) {
      return this.field_227522_d_.apply(p_227541_1_);
   }

   public ValidationTracker func_227529_a_(LootParameterSet p_227529_1_) {
      return new ValidationTracker(this.field_227519_a_, this.field_227520_b_, p_227529_1_, this.field_227522_d_, this.field_227523_e_, this.field_227524_f_, this.field_227525_g_);
   }
}