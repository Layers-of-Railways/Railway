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

public class EnterBlockTrigger extends AbstractCriterionTrigger<EnterBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enter_block");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public EnterBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      Block block = func_226550_a_(json);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.deserializeProperties(json.get("state"));
      if (block != null) {
         statepropertiespredicate.forEachNotPresent(block.getStateContainer(), (p_226548_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_226548_1_);
         });
      }

      return new EnterBlockTrigger.Instance(block, statepropertiespredicate);
   }

   @Nullable
   private static Block func_226550_a_(JsonObject p_226550_0_) {
      if (p_226550_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(p_226550_0_, "block"));
         return Registry.BLOCK.getValue(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity player, BlockState state) {
      this.func_227070_a_(player.getAdvancements(), (p_226549_1_) -> {
         return p_226549_1_.test(state);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate properties;

      public Instance(@Nullable Block p_i225733_1_, StatePropertiesPredicate p_i225733_2_) {
         super(EnterBlockTrigger.ID);
         this.block = p_i225733_1_;
         this.properties = p_i225733_2_;
      }

      public static EnterBlockTrigger.Instance forBlock(Block p_203920_0_) {
         return new EnterBlockTrigger.Instance(p_203920_0_, StatePropertiesPredicate.EMPTY);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.properties.toJsonElement());
         return jsonobject;
      }

      public boolean test(BlockState state) {
         if (this.block != null && state.getBlock() != this.block) {
            return false;
         } else {
            return this.properties.matches(state);
         }
      }
   }
}