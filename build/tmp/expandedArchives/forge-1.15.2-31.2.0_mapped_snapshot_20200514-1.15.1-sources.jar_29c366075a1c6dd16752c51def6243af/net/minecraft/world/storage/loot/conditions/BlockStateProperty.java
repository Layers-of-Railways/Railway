package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class BlockStateProperty implements ILootCondition {
   private final Block block;
   private final StatePropertiesPredicate properties;

   private BlockStateProperty(Block p_i225896_1_, StatePropertiesPredicate p_i225896_2_) {
      this.block = p_i225896_1_;
      this.properties = p_i225896_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.BLOCK_STATE);
   }

   public boolean test(LootContext p_test_1_) {
      BlockState blockstate = p_test_1_.get(LootParameters.BLOCK_STATE);
      return blockstate != null && this.block == blockstate.getBlock() && this.properties.matches(blockstate);
   }

   public static BlockStateProperty.Builder builder(Block blockIn) {
      return new BlockStateProperty.Builder(blockIn);
   }

   public static class Builder implements ILootCondition.IBuilder {
      private final Block block;
      private StatePropertiesPredicate desiredProperties = StatePropertiesPredicate.EMPTY;

      public Builder(Block blockIn) {
         this.block = blockIn;
      }

      public BlockStateProperty.Builder fromProperties(StatePropertiesPredicate.Builder p_227567_1_) {
         this.desiredProperties = p_227567_1_.build();
         return this;
      }

      public ILootCondition build() {
         return new BlockStateProperty(this.block, this.desiredProperties);
      }
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<BlockStateProperty> {
      protected Serializer() {
         super(new ResourceLocation("block_state_property"), BlockStateProperty.class);
      }

      public void serialize(JsonObject json, BlockStateProperty value, JsonSerializationContext context) {
         json.addProperty("block", Registry.BLOCK.getKey(value.block).toString());
         json.add("properties", value.properties.toJsonElement());
      }

      public BlockStateProperty deserialize(JsonObject json, JsonDeserializationContext context) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "block"));
         Block block = Registry.BLOCK.getValue(resourcelocation).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + resourcelocation);
         });
         StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.deserializeProperties(json.get("properties"));
         statepropertiespredicate.forEachNotPresent(block.getStateContainer(), (p_227568_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_227568_1_);
         });
         return new BlockStateProperty(block, statepropertiespredicate);
      }
   }
}