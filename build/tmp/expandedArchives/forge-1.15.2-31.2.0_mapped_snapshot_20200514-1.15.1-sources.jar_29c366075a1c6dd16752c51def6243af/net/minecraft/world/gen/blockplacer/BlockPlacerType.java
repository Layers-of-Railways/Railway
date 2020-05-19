package net.minecraft.world.gen.blockplacer;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class BlockPlacerType<P extends BlockPlacer> {
   public static final BlockPlacerType<SimpleBlockPlacer> SIMPLE_BLOCK = register("simple_block_placer", SimpleBlockPlacer::new);
   public static final BlockPlacerType<DoublePlantBlockPlacer> DOUBLE_PLANT = register("double_plant_placer", DoublePlantBlockPlacer::new);
   public static final BlockPlacerType<ColumnBlockPlacer> COLUMN = register("column_placer", ColumnBlockPlacer::new);
   private final Function<Dynamic<?>, P> field_227262_d_;

   private static <P extends BlockPlacer> BlockPlacerType<P> register(String p_227264_0_, Function<Dynamic<?>, P> p_227264_1_) {
      return Registry.register(Registry.BLOCK_PLACER_TYPE, p_227264_0_, new BlockPlacerType<>(p_227264_1_));
   }

   private BlockPlacerType(Function<Dynamic<?>, P> p_i225825_1_) {
      this.field_227262_d_ = p_i225825_1_;
   }

   public P func_227263_a_(Dynamic<?> p_227263_1_) {
      return (P)(this.field_227262_d_.apply(p_227263_1_));
   }
}