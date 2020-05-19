package net.minecraft.world.gen.feature.template;

import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface IRuleTestType extends IDynamicDeserializer<RuleTest> {
   IRuleTestType ALWAYS_TRUE = register("always_true", (p_214909_0_) -> {
      return AlwaysTrueRuleTest.INSTANCE;
   });
   IRuleTestType BLOCK_MATCH = register("block_match", BlockMatchRuleTest::new);
   IRuleTestType BLOCKSTATE_MATCH = register("blockstate_match", BlockStateMatchRuleTest::new);
   IRuleTestType TAG_MATCH = register("tag_match", TagMatchRuleTest::new);
   IRuleTestType RANDOM_BLOCK_MATCH = register("random_block_match", RandomBlockMatchRuleTest::new);
   IRuleTestType RANDOM_BLOCKSTATE_MATCH = register("random_blockstate_match", RandomBlockStateMatchRuleTest::new);

   static IRuleTestType register(String key, IRuleTestType type) {
      return Registry.register(Registry.RULE_TEST, key, type);
   }
}