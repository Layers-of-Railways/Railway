package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.BlockState;

public interface ISurfaceBuilderConfig {
   BlockState getTop();

   BlockState getUnder();
}