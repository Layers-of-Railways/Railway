package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WeightedList<U> {
   protected final List<WeightedList<U>.Entry<? extends U>> field_220658_a = Lists.newArrayList();
   private final Random random;

   public WeightedList(Random p_i50335_1_) {
      this.random = p_i50335_1_;
   }

   public WeightedList() {
      this(new Random());
   }

   public <T> WeightedList(Dynamic<T> p_i225709_1_, Function<Dynamic<T>, U> p_i225709_2_) {
      this();
      p_i225709_1_.asStream().forEach((p_226316_2_) -> {
         p_226316_2_.get("data").map((p_226317_3_) -> {
            U u = p_i225709_2_.apply(p_226317_3_);
            int i = p_226316_2_.get("weight").asInt(1);
            return (U)this.func_226313_a_(u, i);
         });
      });
   }

   public <T> T func_226310_a_(DynamicOps<T> p_226310_1_, Function<U, Dynamic<T>> p_226310_2_) {
      return p_226310_1_.createList(this.func_226319_c_().map((p_226311_2_) -> {
         return p_226310_1_.createMap(ImmutableMap.<T, T>builder().put(p_226310_1_.createString("data"), p_226310_2_.apply((U)p_226311_2_.func_220647_b()).getValue()).put(p_226310_1_.createString("weight"), p_226310_1_.createInt(p_226311_2_.func_226322_b_())).build());
      }));
   }

   public WeightedList<U> func_226313_a_(U p_226313_1_, int p_226313_2_) {
      this.field_220658_a.add(new WeightedList.Entry(p_226313_1_, p_226313_2_));
      return this;
   }

   public WeightedList<U> func_226309_a_() {
      return this.func_226314_a_(this.random);
   }

   public WeightedList<U> func_226314_a_(Random p_226314_1_) {
      this.field_220658_a.forEach((p_226315_1_) -> {
         p_226315_1_.func_220648_a(p_226314_1_.nextFloat());
      });
      this.field_220658_a.sort(Comparator.comparingDouble((p_226312_0_) -> {
         return p_226312_0_.func_220649_a();
      }));
      return this;
   }

   public Stream<? extends U> func_220655_b() {
      return this.field_220658_a.stream().map(WeightedList.Entry::func_220647_b);
   }

   public Stream<WeightedList<U>.Entry<? extends U>> func_226319_c_() {
      return this.field_220658_a.stream();
   }

   public U func_226318_b_(Random p_226318_1_) {
      return (U)this.func_226314_a_(p_226318_1_).func_220655_b().findFirst().orElseThrow(RuntimeException::new);
   }

   public String toString() {
      return "WeightedList[" + this.field_220658_a + "]";
   }

   public class Entry<T> {
      private final T field_220651_b;
      private final int field_220652_c;
      private double field_220653_d;

      private Entry(T p_i50545_2_, int p_i50545_3_) {
         this.field_220652_c = p_i50545_3_;
         this.field_220651_b = p_i50545_2_;
      }

      private double func_220649_a() {
         return this.field_220653_d;
      }

      private void func_220648_a(float p_220648_1_) {
         this.field_220653_d = -Math.pow((double)p_220648_1_, (double)(1.0F / (float)this.field_220652_c));
      }

      public T func_220647_b() {
         return this.field_220651_b;
      }

      public int func_226322_b_() {
         return this.field_220652_c;
      }

      public String toString() {
         return "" + this.field_220652_c + ":" + this.field_220651_b;
      }
   }
}