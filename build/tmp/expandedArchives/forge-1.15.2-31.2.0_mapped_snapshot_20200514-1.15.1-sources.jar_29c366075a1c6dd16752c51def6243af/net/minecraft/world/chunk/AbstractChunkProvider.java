package net.minecraft.world.chunk;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractChunkProvider implements IChunkLightProvider, AutoCloseable {
   @Nullable
   public Chunk getChunk(int chunkX, int chunkZ, boolean load) {
      return (Chunk)this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, load);
   }

   /**
    * Gets a chunk without loading it.
    */
   @Nullable
   public Chunk getChunkWithoutLoading(int chunkX, int chunkZ) {
      return this.getChunk(chunkX, chunkZ, false);
   }

   @Nullable
   public IBlockReader getChunkForLight(int chunkX, int chunkZ) {
      return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
   }

   /**
    * Checks to see if a chunk exists at x, z
    */
   public boolean chunkExists(int x, int z) {
      return this.getChunk(x, z, ChunkStatus.FULL, false) != null;
   }

   @Nullable
   public abstract IChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load);

   @OnlyIn(Dist.CLIENT)
   public abstract void tick(BooleanSupplier hasTimeLeft);

   /**
    * Converts the instance data to a readable string.
    */
   public abstract String makeString();

   public void close() throws IOException {
   }

   public abstract WorldLightManager getLightManager();

   public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
   }

   public void forceChunk(ChunkPos pos, boolean add) {
   }

   public boolean isChunkLoaded(Entity entityIn) {
      return true;
   }

   public boolean isChunkLoaded(ChunkPos pos) {
      return true;
   }

   public boolean canTick(BlockPos pos) {
      return true;
   }
}