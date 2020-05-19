package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.potion.EffectInstance;
import org.apache.commons.lang3.tuple.Pair;

public class Food {
   private final int value;
   private final float saturation;
   private final boolean meat;
   private final boolean canEatWhenFull;
   private final boolean fastToEat;
   private final List<Pair<java.util.function.Supplier<EffectInstance>, Float>> effects;
   
   private Food(Food.Builder builder) {
	   this.value = builder.value;
	   this.saturation = builder.saturation;
	   this.meat = builder.meat;
	   this.canEatWhenFull = builder.alwaysEdible;
	   this.fastToEat = builder.fastToEat;
	   this.effects = builder.effects;
   }

   // Forge: Use builder method instead
   @Deprecated
   private Food(int healing, float saturationIn, boolean isMeat, boolean alwaysEdible, boolean fastEdible, List<Pair<EffectInstance, Float>> effectsIn) {
      this.value = healing;
      this.saturation = saturationIn;
      this.meat = isMeat;
      this.canEatWhenFull = alwaysEdible;
      this.fastToEat = fastEdible;
      this.effects = effectsIn.stream().map(pair -> Pair.<java.util.function.Supplier<EffectInstance>, Float>of(pair::getLeft, pair.getRight())).collect(java.util.stream.Collectors.toList());
   }

   public int getHealing() {
      return this.value;
   }

   public float getSaturation() {
      return this.saturation;
   }

   public boolean isMeat() {
      return this.meat;
   }

   public boolean canEatWhenFull() {
      return this.canEatWhenFull;
   }

   public boolean isFastEating() {
      return this.fastToEat;
   }

   public List<Pair<EffectInstance, Float>> getEffects() {
      return this.effects.stream().map(pair -> Pair.of(pair.getLeft() != null ? pair.getLeft().get() : null, pair.getRight())).collect(java.util.stream.Collectors.toList());
   }

   public static class Builder {
      private int value;
      private float saturation;
      private boolean meat;
      private boolean alwaysEdible;
      private boolean fastToEat;
      private final List<Pair<java.util.function.Supplier<EffectInstance>, Float>> effects = Lists.newArrayList();

      public Food.Builder hunger(int hungerIn) {
         this.value = hungerIn;
         return this;
      }

      public Food.Builder saturation(float saturationIn) {
         this.saturation = saturationIn;
         return this;
      }

      public Food.Builder meat() {
         this.meat = true;
         return this;
      }

      public Food.Builder setAlwaysEdible() {
         this.alwaysEdible = true;
         return this;
      }

      public Food.Builder fastToEat() {
         this.fastToEat = true;
         return this;
      }
      
      public Food.Builder effect(java.util.function.Supplier<EffectInstance> effectIn, float probability) {
          this.effects.add(Pair.of(effectIn, probability));
          return this;
       }

      // Forge: Use supplier method instead
      @Deprecated
      public Food.Builder effect(EffectInstance effectIn, float probability) {
         this.effects.add(Pair.of(() -> effectIn, probability));
         return this;
      }

      public Food build() {
         return new Food(this);
      }
   }
}