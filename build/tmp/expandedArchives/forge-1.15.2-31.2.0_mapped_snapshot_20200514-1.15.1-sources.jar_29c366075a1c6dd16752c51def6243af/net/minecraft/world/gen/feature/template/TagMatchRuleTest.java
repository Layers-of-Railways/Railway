package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class TagMatchRuleTest extends RuleTest {
   private final Tag<Block> tag;

   public TagMatchRuleTest(Tag<Block> tag) {
      this.tag = tag;
   }

   public <T> TagMatchRuleTest(Dynamic<T> p_i51319_1_) {
      this(BlockTags.getCollection().get(new ResourceLocation(p_i51319_1_.get("tag").asString(""))));
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_.isIn(this.tag);
   }

   protected IRuleTestType getType() {
      return IRuleTestType.TAG_MATCH;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> p_215182_1_) {
      return new Dynamic<>(p_215182_1_, p_215182_1_.createMap(ImmutableMap.of(p_215182_1_.createString("tag"), p_215182_1_.createString(this.tag.getId().toString()))));
   }
}