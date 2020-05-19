package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public class RuleEntry {
   private final RuleTest inputPredicate;
   private final RuleTest locationPredicate;
   private final BlockState outputState;
   @Nullable
   private final CompoundNBT outputNbt;

   public RuleEntry(RuleTest inputPredicate, RuleTest locationPredicate, BlockState outputState) {
      this(inputPredicate, locationPredicate, outputState, (CompoundNBT)null);
   }

   public RuleEntry(RuleTest inputPredicate, RuleTest locationPredicate, BlockState outputState, @Nullable CompoundNBT outputNbt) {
      this.inputPredicate = inputPredicate;
      this.locationPredicate = locationPredicate;
      this.outputState = outputState;
      this.outputNbt = outputNbt;
   }

   public boolean test(BlockState stateA, BlockState stateB, Random rand) {
      return this.inputPredicate.test(stateA, rand) && this.locationPredicate.test(stateB, rand);
   }

   public BlockState getOutputState() {
      return this.outputState;
   }

   @Nullable
   public CompoundNBT getOutputNbt() {
      return this.outputNbt;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      T t = ops.createMap(ImmutableMap.of(ops.createString("input_predicate"), this.inputPredicate.serialize(ops).getValue(), ops.createString("location_predicate"), this.locationPredicate.serialize(ops).getValue(), ops.createString("output_state"), BlockState.serialize(ops, this.outputState).getValue()));
      return this.outputNbt == null ? new Dynamic<>(ops, t) : new Dynamic<>(ops, ops.mergeInto(t, ops.createString("output_nbt"), (new Dynamic<>(NBTDynamicOps.INSTANCE, this.outputNbt)).convert(ops).getValue()));
   }

   public static <T> RuleEntry deserialize(Dynamic<T> p_215213_0_) {
      Dynamic<T> dynamic = p_215213_0_.get("input_predicate").orElseEmptyMap();
      Dynamic<T> dynamic1 = p_215213_0_.get("location_predicate").orElseEmptyMap();
      RuleTest ruletest = IDynamicDeserializer.func_214907_a(dynamic, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
      RuleTest ruletest1 = IDynamicDeserializer.func_214907_a(dynamic1, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
      BlockState blockstate = BlockState.deserialize(p_215213_0_.get("output_state").orElseEmptyMap());
      CompoundNBT compoundnbt = (CompoundNBT)p_215213_0_.get("output_nbt").map((p_215210_0_) -> {
         return p_215210_0_.convert(NBTDynamicOps.INSTANCE).getValue();
      }).orElse((INBT)null);
      return new RuleEntry(ruletest, ruletest1, blockstate, compoundnbt);
   }
}