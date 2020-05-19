package net.minecraft.potion;

public class InstantEffect extends Effect {
   public InstantEffect(EffectType p_i50392_1_, int p_i50392_2_) {
      super(p_i50392_1_, p_i50392_2_);
   }

   /**
    * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
    */
   public boolean isInstant() {
      return true;
   }

   /**
    * checks if Potion effect is ready to be applied this tick.
    */
   public boolean isReady(int duration, int amplifier) {
      return duration >= 1;
   }
}