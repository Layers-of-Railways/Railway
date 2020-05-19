package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

public class ChangeDimensionTrigger extends AbstractCriterionTrigger<ChangeDimensionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("changed_dimension");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public ChangeDimensionTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      DimensionType dimensiontype = json.has("from") ? DimensionType.byName(new ResourceLocation(JSONUtils.getString(json, "from"))) : null;
      DimensionType dimensiontype1 = json.has("to") ? DimensionType.byName(new ResourceLocation(JSONUtils.getString(json, "to"))) : null;
      return new ChangeDimensionTrigger.Instance(dimensiontype, dimensiontype1);
   }

   public void trigger(ServerPlayerEntity player, DimensionType from, DimensionType to) {
      this.func_227070_a_(player.getAdvancements(), (p_226305_2_) -> {
         return p_226305_2_.test(from, to);
      });
   }

   public static class Instance extends CriterionInstance {
      @Nullable
      private final DimensionType from;
      @Nullable
      private final DimensionType to;

      public Instance(@Nullable DimensionType from, @Nullable DimensionType to) {
         super(ChangeDimensionTrigger.ID);
         this.from = from;
         this.to = to;
      }

      public static ChangeDimensionTrigger.Instance changedDimensionTo(DimensionType p_203911_0_) {
         return new ChangeDimensionTrigger.Instance((DimensionType)null, p_203911_0_);
      }

      public boolean test(DimensionType from, DimensionType to) {
         if (this.from != null && this.from != from) {
            return false;
         } else {
            return this.to == null || this.to == to;
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.from != null) {
            jsonobject.addProperty("from", DimensionType.getKey(this.from).toString());
         }

         if (this.to != null) {
            jsonobject.addProperty("to", DimensionType.getKey(this.to).toString());
         }

         return jsonobject;
      }
   }
}