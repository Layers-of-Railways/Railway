package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SurfaceBuilderConfig implements ISurfaceBuilderConfig {
   private final BlockState topMaterial;
   private final BlockState underMaterial;
   private final BlockState underWaterMaterial;

   public SurfaceBuilderConfig(BlockState topMaterial, BlockState underMaterial, BlockState underWaterMaterial) {
      this.topMaterial = topMaterial;
      this.underMaterial = underMaterial;
      this.underWaterMaterial = underWaterMaterial;
   }

   public BlockState getTop() {
      return this.topMaterial;
   }

   public BlockState getUnder() {
      return this.underMaterial;
   }

   public BlockState getUnderWaterMaterial() {
      return this.underWaterMaterial;
   }

   public static SurfaceBuilderConfig deserialize(Dynamic<?> p_215455_0_) {
      BlockState blockstate = p_215455_0_.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      BlockState blockstate1 = p_215455_0_.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      BlockState blockstate2 = p_215455_0_.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      return new SurfaceBuilderConfig(blockstate, blockstate1, blockstate2);
   }
}