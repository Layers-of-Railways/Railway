package net.minecraft.entity.ai.attributes;

import javax.annotation.Nullable;

public abstract class Attribute implements IAttribute {
   private final IAttribute parent;
   private final String translationKey;
   private final double defaultValue;
   private boolean shouldWatch;

   protected Attribute(@Nullable IAttribute parentIn, String unlocalizedNameIn, double defaultValueIn) {
      this.parent = parentIn;
      this.translationKey = unlocalizedNameIn;
      this.defaultValue = defaultValueIn;
      if (unlocalizedNameIn == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   public String getName() {
      return this.translationKey;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean getShouldWatch() {
      return this.shouldWatch;
   }

   public Attribute setShouldWatch(boolean shouldWatchIn) {
      this.shouldWatch = shouldWatchIn;
      return this;
   }

   @Nullable
   public IAttribute getParent() {
      return this.parent;
   }

   public int hashCode() {
      return this.translationKey.hashCode();
   }

   public boolean equals(Object p_equals_1_) {
      return p_equals_1_ instanceof IAttribute && this.translationKey.equals(((IAttribute)p_equals_1_).getName());
   }
}