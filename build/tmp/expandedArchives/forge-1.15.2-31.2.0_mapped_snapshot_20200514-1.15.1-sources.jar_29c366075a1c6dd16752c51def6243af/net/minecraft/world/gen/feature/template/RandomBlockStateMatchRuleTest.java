package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class RandomBlockStateMatchRuleTest extends RuleTest {
   private final BlockState state;
   private final float probability;

   public RandomBlockStateMatchRuleTest(BlockState state, float probability) {
      this.state = state;
      this.probability = probability;
   }

   public <T> RandomBlockStateMatchRuleTest(Dynamic<T> p_i51323_1_) {
      this(BlockState.deserialize(p_i51323_1_.get("blockstate").orElseEmptyMap()), p_i51323_1_.get("probability").asFloat(1.0F));
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_ == this.state && p_215181_2_.nextFloat() < this.probability;
   }

   protected IRuleTestType getType() {
      return IRuleTestType.RANDOM_BLOCKSTATE_MATCH;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> p_215182_1_) {
      return new Dynamic<>(p_215182_1_, p_215182_1_.createMap(ImmutableMap.of(p_215182_1_.createString("blockstate"), BlockState.serialize(p_215182_1_, this.state).getValue(), p_215182_1_.createString("probability"), p_215182_1_.createFloat(this.probability))));
   }
}