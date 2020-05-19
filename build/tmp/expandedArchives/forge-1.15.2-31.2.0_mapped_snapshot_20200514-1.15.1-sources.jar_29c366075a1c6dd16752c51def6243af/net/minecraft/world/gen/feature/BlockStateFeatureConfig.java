package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockStateFeatureConfig implements IFeatureConfig {
   public final BlockState state;

   public BlockStateFeatureConfig(BlockState p_i225831_1_) {
      this.state = p_i225831_1_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
   }

   public static <T> BlockStateFeatureConfig deserialize(Dynamic<T> p_227271_0_) {
      BlockState blockstate = p_227271_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      return new BlockStateFeatureConfig(blockstate);
   }
}