package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SlideDownBlockTrigger extends AbstractCriterionTrigger<SlideDownBlockTrigger.Instance> {
   private static final ResourceLocation field_227147_a_ = new ResourceLocation("slide_down_block");

   public ResourceLocation getId() {
      return field_227147_a_;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public SlideDownBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      Block block = func_227150_a_(json);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.deserializeProperties(json.get("state"));
      if (block != null) {
         statepropertiespredicate.forEachNotPresent(block.getStateContainer(), (p_227148_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_227148_1_);
         });
      }

      return new SlideDownBlockTrigger.Instance(block, statepropertiespredicate);
   }

   @Nullable
   private static Block func_227150_a_(JsonObject p_227150_0_) {
      if (p_227150_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(p_227150_0_, "block"));
         return Registry.BLOCK.getValue(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void func_227152_a_(ServerPlayerEntity p_227152_1_, BlockState p_227152_2_) {
      this.func_227070_a_(p_227152_1_.getAdvancements(), (p_227149_1_) -> {
         return p_227149_1_.func_227157_a_(p_227152_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block field_227154_a_;
      private final StatePropertiesPredicate field_227155_b_;

      public Instance(@Nullable Block p_i225786_1_, StatePropertiesPredicate p_i225786_2_) {
         super(SlideDownBlockTrigger.field_227147_a_);
         this.field_227154_a_ = p_i225786_1_;
         this.field_227155_b_ = p_i225786_2_;
      }

      public static SlideDownBlockTrigger.Instance func_227156_a_(Block p_227156_0_) {
         return new SlideDownBlockTrigger.Instance(p_227156_0_, StatePropertiesPredicate.EMPTY);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.field_227154_a_ != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.field_227154_a_).toString());
         }

         jsonobject.add("state", this.field_227155_b_.toJsonElement());
         return jsonobject;
      }

      public boolean func_227157_a_(BlockState p_227157_1_) {
         if (this.field_227154_a_ != null && p_227157_1_.getBlock() != this.field_227154_a_) {
            return false;
         } else {
            return this.field_227155_b_.matches(p_227157_1_);
         }
      }
   }
}