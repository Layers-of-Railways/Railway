package net.minecraft.world.gen.blockstateprovider;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;

public abstract class BlockStateProvider implements IDynamicSerializable {
   protected final BlockStateProviderType<?> blockStateProvider;

   protected BlockStateProvider(BlockStateProviderType<?> p_i225854_1_) {
      this.blockStateProvider = p_i225854_1_;
   }

   public abstract BlockState getBlockState(Random randomIn, BlockPos blockPosIn);
}