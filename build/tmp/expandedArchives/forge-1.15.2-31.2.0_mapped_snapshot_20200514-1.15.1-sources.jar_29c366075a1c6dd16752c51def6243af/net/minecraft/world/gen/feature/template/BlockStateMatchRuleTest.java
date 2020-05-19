package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class BlockStateMatchRuleTest extends RuleTest {
   private final BlockState state;

   public BlockStateMatchRuleTest(BlockState state) {
      this.state = state;
   }

   public <T> BlockStateMatchRuleTest(Dynamic<T> p_i51331_1_) {
      this(BlockState.deserialize(p_i51331_1_.get("blockstate").orElseEmptyMap()));
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_ == this.state;
   }

   protected IRuleTestType getType() {
      return IRuleTestType.BLOCKSTATE_MATCH;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> p_215182_1_) {
      return new Dynamic<>(p_215182_1_, p_215182_1_.createMap(ImmutableMap.of(p_215182_1_.createString("blockstate"), BlockState.serialize(p_215182_1_, this.state).getValue())));
   }
}