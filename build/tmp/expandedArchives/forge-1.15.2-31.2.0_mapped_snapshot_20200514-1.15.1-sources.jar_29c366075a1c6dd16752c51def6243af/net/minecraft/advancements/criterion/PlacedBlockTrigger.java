package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class PlacedBlockTrigger extends AbstractCriterionTrigger<PlacedBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("placed_block");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public PlacedBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      Block block = func_226950_a_(json);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.deserializeProperties(json.get("state"));
      if (block != null) {
         statepropertiespredicate.forEachNotPresent(block.getStateContainer(), (p_226948_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_226948_1_ + ":");
         });
      }

      LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("location"));
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new PlacedBlockTrigger.Instance(block, statepropertiespredicate, locationpredicate, itempredicate);
   }

   @Nullable
   private static Block func_226950_a_(JsonObject p_226950_0_) {
      if (p_226950_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(p_226950_0_, "block"));
         return Registry.BLOCK.getValue(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity player, BlockPos pos, ItemStack item) {
      BlockState blockstate = player.getServerWorld().getBlockState(pos);
      this.func_227070_a_(player.getAdvancements(), (p_226949_4_) -> {
         return p_226949_4_.test(blockstate, pos, player.getServerWorld(), item);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate properties;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public Instance(@Nullable Block p_i225765_1_, StatePropertiesPredicate p_i225765_2_, LocationPredicate p_i225765_3_, ItemPredicate p_i225765_4_) {
         super(PlacedBlockTrigger.ID);
         this.block = p_i225765_1_;
         this.properties = p_i225765_2_;
         this.location = p_i225765_3_;
         this.item = p_i225765_4_;
      }

      public static PlacedBlockTrigger.Instance placedBlock(Block p_203934_0_) {
         return new PlacedBlockTrigger.Instance(p_203934_0_, StatePropertiesPredicate.EMPTY, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean test(BlockState state, BlockPos pos, ServerWorld world, ItemStack item) {
         if (this.block != null && state.getBlock() != this.block) {
            return false;
         } else if (!this.properties.matches(state)) {
            return false;
         } else if (!this.location.test(world, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ())) {
            return false;
         } else {
            return this.item.test(item);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.properties.toJsonElement());
         jsonobject.add("location", this.location.serialize());
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }
}