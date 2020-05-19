package net.minecraft.world.gen.blockstateprovider;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> {
   public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleBlockStateProvider::new);
   public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", WeightedBlockStateProvider::new);
   public static final BlockStateProviderType<PlainFlowerBlockStateProvider> PLAIN_FLOWER_PROVIDER = register("plain_flower_provider", PlainFlowerBlockStateProvider::new);
   public static final BlockStateProviderType<ForestFlowerBlockStateProvider> FOREST_FLOWER_PROVIDER = register("forest_flower_provider", ForestFlowerBlockStateProvider::new);
   private final Function<Dynamic<?>, P> field_227398_e_;

   private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String p_227400_0_, Function<Dynamic<?>, P> p_227400_1_) {
      return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, p_227400_0_, new BlockStateProviderType<>(p_227400_1_));
   }

   private BlockStateProviderType(Function<Dynamic<?>, P> p_i225855_1_) {
      this.field_227398_e_ = p_i225855_1_;
   }

   public P func_227399_a_(Dynamic<?> p_227399_1_) {
      return (P)(this.field_227398_e_.apply(p_227399_1_));
   }
}