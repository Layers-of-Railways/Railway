package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAttributeInstance {
   /**
    * Get the Attribute this is an instance of
    */
   IAttribute getAttribute();

   double getBaseValue();

   void setBaseValue(double baseValue);

   Set<AttributeModifier> func_225504_a_(AttributeModifier.Operation p_225504_1_);

   Set<AttributeModifier> func_225505_c_();

   boolean hasModifier(AttributeModifier modifier);

   /**
    * Returns attribute modifier, if any, by the given UUID
    */
   @Nullable
   AttributeModifier getModifier(UUID uuid);

   void applyModifier(AttributeModifier modifier);

   void removeModifier(AttributeModifier modifier);

   void removeModifier(UUID p_188479_1_);

   @OnlyIn(Dist.CLIENT)
   void removeAllModifiers();

   double getValue();

   @OnlyIn(Dist.CLIENT)
   default void func_226302_a_(IAttributeInstance p_226302_1_) {
      this.setBaseValue(p_226302_1_.getBaseValue());
      Set<AttributeModifier> set = p_226302_1_.func_225505_c_();
      Set<AttributeModifier> set1 = this.func_225505_c_();
      ImmutableSet<AttributeModifier> immutableset = ImmutableSet.copyOf(Sets.difference(set, set1));
      ImmutableSet<AttributeModifier> immutableset1 = ImmutableSet.copyOf(Sets.difference(set1, set));
      immutableset.forEach(this::applyModifier);
      immutableset1.forEach(this::removeModifier);
   }
}