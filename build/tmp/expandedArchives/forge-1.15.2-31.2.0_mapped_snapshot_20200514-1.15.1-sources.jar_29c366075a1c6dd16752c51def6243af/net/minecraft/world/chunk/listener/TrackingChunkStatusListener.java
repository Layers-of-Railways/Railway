package net.minecraft.world.chunk.listener;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrackingChunkStatusListener implements IChunkStatusListener {
   private final LoggingChunkStatusListener loggingListener;
   private final Long2ObjectOpenHashMap<ChunkStatus> statuses;
   private ChunkPos center = new ChunkPos(0, 0);
   private final int diameter;
   private final int positionOffset;
   private final int field_219531_f;
   private boolean tracking;

   public TrackingChunkStatusListener(int radius) {
      this.loggingListener = new LoggingChunkStatusListener(radius);
      this.diameter = radius * 2 + 1;
      this.positionOffset = radius + ChunkStatus.maxDistance();
      this.field_219531_f = this.positionOffset * 2 + 1;
      this.statuses = new Long2ObjectOpenHashMap<>();
   }

   public void start(ChunkPos center) {
      if (this.tracking) {
         this.loggingListener.start(center);
         this.center = center;
      }
   }

   public void statusChanged(ChunkPos chunkPosition, @Nullable ChunkStatus newStatus) {
      if (this.tracking) {
         this.loggingListener.statusChanged(chunkPosition, newStatus);
         if (newStatus == null) {
            this.statuses.remove(chunkPosition.asLong());
         } else {
            this.statuses.put(chunkPosition.asLong(), newStatus);
         }

      }
   }

   public void startTracking() {
      this.tracking = true;
      this.statuses.clear();
   }

   public void stop() {
      this.tracking = false;
      this.loggingListener.stop();
   }

   public int getDiameter() {
      return this.diameter;
   }

   public int func_219523_d() {
      return this.field_219531_f;
   }

   public int getPercentDone() {
      return this.loggingListener.getPercentDone();
   }

   @Nullable
   public ChunkStatus getStatus(int x, int z) {
      return this.statuses.get(ChunkPos.asLong(x + this.center.x - this.positionOffset, z + this.center.z - this.positionOffset));
   }
}