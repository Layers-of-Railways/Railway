package net.minecraft.world.chunk.listener;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public interface IChunkStatusListener {
   void start(ChunkPos center);

   void statusChanged(ChunkPos chunkPosition, @Nullable ChunkStatus newStatus);

   void stop();
}