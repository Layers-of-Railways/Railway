package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.ValidationTracker;

public class Inverted implements ILootCondition {
   private final ILootCondition term;

   private Inverted(ILootCondition term) {
      this.term = term;
   }

   public final boolean test(LootContext p_test_1_) {
      return !this.term.test(p_test_1_);
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return this.term.getRequiredParameters();
   }

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      ILootCondition.super.func_225580_a_(p_225580_1_);
      this.term.func_225580_a_(p_225580_1_);
   }

   public static ILootCondition.IBuilder builder(ILootCondition.IBuilder p_215979_0_) {
      Inverted inverted = new Inverted(p_215979_0_.build());
      return () -> {
         return inverted;
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<Inverted> {
      public Serializer() {
         super(new ResourceLocation("inverted"), Inverted.class);
      }

      public void serialize(JsonObject json, Inverted value, JsonSerializationContext context) {
         json.add("term", context.serialize(value.term));
      }

      public Inverted deserialize(JsonObject json, JsonDeserializationContext context) {
         ILootCondition ilootcondition = JSONUtils.deserializeClass(json, "term", context, ILootCondition.class);
         return new Inverted(ilootcondition);
      }
   }
}