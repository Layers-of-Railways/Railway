package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LiquidsConfig implements IFeatureConfig {
   public final IFluidState state;
   public final boolean needsBlockBelow;
   public final int rockAmount;
   public final int holeAmount;
   public final Set<Block> acceptedBlocks;

   public LiquidsConfig(IFluidState p_i225841_1_, boolean p_i225841_2_, int p_i225841_3_, int p_i225841_4_, Set<Block> p_i225841_5_) {
      this.state = p_i225841_1_;
      this.needsBlockBelow = p_i225841_2_;
      this.rockAmount = p_i225841_3_;
      this.holeAmount = p_i225841_4_;
      this.acceptedBlocks = p_i225841_5_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("state"), IFluidState.serialize(ops, this.state).getValue(), ops.createString("requires_block_below"), ops.createBoolean(this.needsBlockBelow), ops.createString("rock_count"), ops.createInt(this.rockAmount), ops.createString("hole_count"), ops.createInt(this.holeAmount), ops.createString("valid_blocks"), ops.createList(this.acceptedBlocks.stream().map(Registry.BLOCK::getKey).map(ResourceLocation::toString).map(ops::createString)))));
   }

   public static <T> LiquidsConfig deserialize(Dynamic<T> p_214677_0_) {
      return new LiquidsConfig(p_214677_0_.get("state").map(IFluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()), p_214677_0_.get("requires_block_below").asBoolean(true), p_214677_0_.get("rock_count").asInt(4), p_214677_0_.get("hole_count").asInt(1), ImmutableSet.copyOf(p_214677_0_.get("valid_blocks").asList((p_227367_0_) -> {
         return Registry.BLOCK.getOrDefault(new ResourceLocation(p_227367_0_.asString("minecraft:air")));
      })));
   }
}