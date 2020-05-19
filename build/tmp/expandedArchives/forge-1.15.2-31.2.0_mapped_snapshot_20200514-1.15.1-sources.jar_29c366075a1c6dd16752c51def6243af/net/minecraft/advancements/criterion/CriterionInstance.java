package net.minecraft.advancements.criterion;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.util.ResourceLocation;

public class CriterionInstance implements ICriterionInstance {
   private final ResourceLocation criterion;

   public CriterionInstance(ResourceLocation criterionIn) {
      this.criterion = criterionIn;
   }

   public ResourceLocation getId() {
      return this.criterion;
   }

   public String toString() {
      return "AbstractCriterionInstance{criterion=" + this.criterion + '}';
   }
}