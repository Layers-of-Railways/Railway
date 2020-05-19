package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class Potion extends net.minecraftforge.registries.ForgeRegistryEntry<Potion> {
   private final String baseName;
   private final ImmutableList<EffectInstance> effects;

   public static Potion getPotionTypeForName(String name) {
      return Registry.POTION.getOrDefault(ResourceLocation.tryCreate(name));
   }

   public Potion(EffectInstance... effectsIn) {
      this((String)null, effectsIn);
   }

   public Potion(@Nullable String baseNameIn, EffectInstance... effectsIn) {
      this.baseName = baseNameIn;
      this.effects = ImmutableList.copyOf(effectsIn);
   }

   /**
    * Gets the name of this PotionType with a prefix (such as "Splash" or "Lingering") prepended
    */
   public String getNamePrefixed(String prefix) {
      return prefix + (this.baseName == null ? Registry.POTION.getKey(this).getPath() : this.baseName);
   }

   public List<EffectInstance> getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffect() {
      if (!this.effects.isEmpty()) {
         for(EffectInstance effectinstance : this.effects) {
            if (effectinstance.getPotion().isInstant()) {
               return true;
            }
         }
      }

      return false;
   }
}