package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ReplaceBlockConfig implements IFeatureConfig {
   public final BlockState target;
   public final BlockState state;

   public ReplaceBlockConfig(BlockState target, BlockState state) {
      this.target = target;
      this.state = state;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("target"), BlockState.serialize(ops, this.target).getValue(), ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
   }

   public static <T> ReplaceBlockConfig deserialize(Dynamic<T> p_214657_0_) {
      BlockState blockstate = p_214657_0_.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      BlockState blockstate1 = p_214657_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      return new ReplaceBlockConfig(blockstate, blockstate1);
   }
}