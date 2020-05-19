package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SphereReplaceConfig implements IFeatureConfig {
   public final BlockState state;
   public final int radius;
   public final int ySize;
   public final List<BlockState> targets;

   public SphereReplaceConfig(BlockState state, int radiusIn, int ySizeIn, List<BlockState> targetsIn) {
      this.state = state;
      this.radius = radiusIn;
      this.ySize = ySizeIn;
      this.targets = targetsIn;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("state"), BlockState.serialize(ops, this.state).getValue(), ops.createString("radius"), ops.createInt(this.radius), ops.createString("y_size"), ops.createInt(this.ySize), ops.createString("targets"), ops.createList(this.targets.stream().map((p_214692_1_) -> {
         return BlockState.serialize(ops, p_214692_1_).getValue();
      })))));
   }

   public static <T> SphereReplaceConfig deserialize(Dynamic<T> p_214691_0_) {
      BlockState blockstate = p_214691_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      int i = p_214691_0_.get("radius").asInt(0);
      int j = p_214691_0_.get("y_size").asInt(0);
      List<BlockState> list = p_214691_0_.get("targets").asList(BlockState::deserialize);
      return new SphereReplaceConfig(blockstate, i, j, list);
   }
}