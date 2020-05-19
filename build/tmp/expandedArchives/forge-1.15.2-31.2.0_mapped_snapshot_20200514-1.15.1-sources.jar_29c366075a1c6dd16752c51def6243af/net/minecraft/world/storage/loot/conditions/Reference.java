package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.ValidationTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference implements ILootCondition {
   private static final Logger field_227561_a_ = LogManager.getLogger();
   private final ResourceLocation field_227562_b_;

   public Reference(ResourceLocation p_i225894_1_) {
      this.field_227562_b_ = p_i225894_1_;
   }

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      if (p_225580_1_.func_227536_b_(this.field_227562_b_)) {
         p_225580_1_.addProblem("Condition " + this.field_227562_b_ + " is recursively called");
      } else {
         ILootCondition.super.func_225580_a_(p_225580_1_);
         ILootCondition ilootcondition = p_225580_1_.func_227541_d_(this.field_227562_b_);
         if (ilootcondition == null) {
            p_225580_1_.addProblem("Unknown condition table called " + this.field_227562_b_);
         } else {
            ilootcondition.func_225580_a_(p_225580_1_.func_227531_a_(".{" + this.field_227562_b_ + "}", this.field_227562_b_));
         }

      }
   }

   public boolean test(LootContext p_test_1_) {
      ILootCondition ilootcondition = p_test_1_.getLootCondition(this.field_227562_b_);
      if (p_test_1_.addCondition(ilootcondition)) {
         boolean flag;
         try {
            flag = ilootcondition.test(p_test_1_);
         } finally {
            p_test_1_.removeCondition(ilootcondition);
         }

         return flag;
      } else {
         field_227561_a_.warn("Detected infinite loop in loot tables");
         return false;
      }
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<Reference> {
      protected Serializer() {
         super(new ResourceLocation("reference"), Reference.class);
      }

      public void serialize(JsonObject json, Reference value, JsonSerializationContext context) {
         json.addProperty("name", value.field_227562_b_.toString());
      }

      public Reference deserialize(JsonObject json, JsonDeserializationContext context) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "name"));
         return new Reference(resourcelocation);
      }
   }
}