package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class CopyBlockState extends LootFunction {
   private final Block field_227543_a_;
   private final Set<IProperty<?>> field_227544_c_;

   private CopyBlockState(ILootCondition[] p_i225890_1_, Block p_i225890_2_, Set<IProperty<?>> p_i225890_3_) {
      super(p_i225890_1_);
      this.field_227543_a_ = p_i225890_2_;
      this.field_227544_c_ = p_i225890_3_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.BLOCK_STATE);
   }

   protected ItemStack doApply(ItemStack stack, LootContext context) {
      BlockState blockstate = context.get(LootParameters.BLOCK_STATE);
      if (blockstate != null) {
         CompoundNBT compoundnbt = stack.getOrCreateTag();
         CompoundNBT compoundnbt1;
         if (compoundnbt.contains("BlockStateTag", 10)) {
            compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
         } else {
            compoundnbt1 = new CompoundNBT();
            compoundnbt.put("BlockStateTag", compoundnbt1);
         }

         this.field_227544_c_.stream().filter(blockstate::has).forEach((p_227548_2_) -> {
            compoundnbt1.putString(p_227548_2_.getName(), func_227546_a_(blockstate, p_227548_2_));
         });
      }

      return stack;
   }

   public static CopyBlockState.Builder func_227545_a_(Block p_227545_0_) {
      return new CopyBlockState.Builder(p_227545_0_);
   }

   private static <T extends Comparable<T>> String func_227546_a_(BlockState p_227546_0_, IProperty<T> p_227546_1_) {
      T t = p_227546_0_.get(p_227546_1_);
      return p_227546_1_.getName(t);
   }

   public static class Builder extends LootFunction.Builder<CopyBlockState.Builder> {
      private final Block field_227550_a_;
      private final Set<IProperty<?>> field_227551_b_ = Sets.newHashSet();

      private Builder(Block p_i225892_1_) {
         this.field_227550_a_ = p_i225892_1_;
      }

      public CopyBlockState.Builder func_227552_a_(IProperty<?> p_227552_1_) {
         if (!this.field_227550_a_.getStateContainer().getProperties().contains(p_227552_1_)) {
            throw new IllegalStateException("Property " + p_227552_1_ + " is not present on block " + this.field_227550_a_);
         } else {
            this.field_227551_b_.add(p_227552_1_);
            return this;
         }
      }

      protected CopyBlockState.Builder doCast() {
         return this;
      }

      public ILootFunction build() {
         return new CopyBlockState(this.getConditions(), this.field_227550_a_, this.field_227551_b_);
      }
   }

   public static class Serializer extends LootFunction.Serializer<CopyBlockState> {
      public Serializer() {
         super(new ResourceLocation("copy_state"), CopyBlockState.class);
      }

      public void serialize(JsonObject object, CopyBlockState functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.addProperty("block", Registry.BLOCK.getKey(functionClazz.field_227543_a_).toString());
         JsonArray jsonarray = new JsonArray();
         functionClazz.field_227544_c_.forEach((p_227553_1_) -> {
            jsonarray.add(p_227553_1_.getName());
         });
         object.add("properties", jsonarray);
      }

      public CopyBlockState deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(object, "block"));
         Block block = Registry.BLOCK.getValue(resourcelocation).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + resourcelocation);
         });
         StateContainer<Block, BlockState> statecontainer = block.getStateContainer();
         Set<IProperty<?>> set = Sets.newHashSet();
         JsonArray jsonarray = JSONUtils.getJsonArray(object, "properties", (JsonArray)null);
         if (jsonarray != null) {
            jsonarray.forEach((p_227554_2_) -> {
               set.add(statecontainer.getProperty(JSONUtils.getString(p_227554_2_, "property")));
            });
         }

         return new CopyBlockState(conditionsIn, block, set);
      }
   }
}