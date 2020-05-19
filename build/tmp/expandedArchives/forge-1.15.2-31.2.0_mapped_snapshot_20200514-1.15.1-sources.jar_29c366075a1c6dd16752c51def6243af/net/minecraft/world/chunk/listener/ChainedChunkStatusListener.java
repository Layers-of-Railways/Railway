package net.minecraft.world.chunk.listener;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChainedChunkStatusListener implements IChunkStatusListener {
   private final IChunkStatusListener delegate;
   private final DelegatedTaskExecutor<Runnable> executor;

   public ChainedChunkStatusListener(IChunkStatusListener p_i50696_1_, Executor p_i50696_2_) {
      this.delegate = p_i50696_1_;
      this.executor = DelegatedTaskExecutor.create(p_i50696_2_, "progressListener");
   }

   public void start(ChunkPos center) {
      this.executor.enqueue(() -> {
         this.delegate.start(center);
      });
   }

   public void statusChanged(ChunkPos chunkPosition, @Nullable ChunkStatus newStatus) {
      this.executor.enqueue(() -> {
         this.delegate.statusChanged(chunkPosition, newStatus);
      });
   }

   public void stop() {
      this.executor.enqueue(this.delegate::stop);
   }
}