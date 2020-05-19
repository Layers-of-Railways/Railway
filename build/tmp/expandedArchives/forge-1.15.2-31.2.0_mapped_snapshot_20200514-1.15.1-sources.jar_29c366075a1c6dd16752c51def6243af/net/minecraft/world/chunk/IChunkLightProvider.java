package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;

public interface IChunkLightProvider {
   @Nullable
   IBlockReader getChunkForLight(int chunkX, int chunkZ);

   default void markLightChanged(LightType type, SectionPos pos) {
   }

   IBlockReader getWorld();
}