package net.minecraft.client.multiplayer;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientChunkProvider extends AbstractChunkProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Chunk empty;
   private final WorldLightManager lightManager;
   private volatile ClientChunkProvider.ChunkArray array;
   private final ClientWorld world;

   public ClientChunkProvider(ClientWorld clientWorldIn, int viewDistance) {
      this.world = clientWorldIn;
      this.empty = new EmptyChunk(clientWorldIn, new ChunkPos(0, 0));
      this.lightManager = new WorldLightManager(this, true, clientWorldIn.getDimension().hasSkyLight());
      this.array = new ClientChunkProvider.ChunkArray(adjustViewDistance(viewDistance));
   }

   public WorldLightManager getLightManager() {
      return this.lightManager;
   }

   private static boolean isValid(@Nullable Chunk chunkIn, int x, int z) {
      if (chunkIn == null) {
         return false;
      } else {
         ChunkPos chunkpos = chunkIn.getPos();
         return chunkpos.x == x && chunkpos.z == z;
      }
   }

   /**
    * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set
    * to false
    */
   public void unloadChunk(int x, int z) {
      if (this.array.inView(x, z)) {
         int i = this.array.getIndex(x, z);
         Chunk chunk = this.array.get(i);
         if (isValid(chunk, x, z)) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(chunk));
            this.array.unload(i, chunk, (Chunk)null);
         }

      }
   }

   @Nullable
   public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
      if (this.array.inView(chunkX, chunkZ)) {
         Chunk chunk = this.array.get(this.array.getIndex(chunkX, chunkZ));
         if (isValid(chunk, chunkX, chunkZ)) {
            return chunk;
         }
      }

      return load ? this.empty : null;
   }

   public IBlockReader getWorld() {
      return this.world;
   }

   @Nullable
   public Chunk loadChunk(int chunkX, int chunkZ, @Nullable BiomeContainer biomeContainerIn, PacketBuffer packetIn, CompoundNBT nbtTagIn, int sizeIn) {
      if (!this.array.inView(chunkX, chunkZ)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", chunkX, chunkZ);
         return null;
      } else {
         int i = this.array.getIndex(chunkX, chunkZ);
         Chunk chunk = this.array.chunks.get(i);
         if (!isValid(chunk, chunkX, chunkZ)) {
            if (biomeContainerIn == null) {
               LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", chunkX, chunkZ);
               return null;
            }

            chunk = new Chunk(this.world, new ChunkPos(chunkX, chunkZ), biomeContainerIn);
            chunk.read(biomeContainerIn, packetIn, nbtTagIn, sizeIn);
            this.array.replace(i, chunk);
         } else {
            chunk.read(biomeContainerIn, packetIn, nbtTagIn, sizeIn);
         }

         ChunkSection[] achunksection = chunk.getSections();
         WorldLightManager worldlightmanager = this.getLightManager();
         worldlightmanager.enableLightSources(new ChunkPos(chunkX, chunkZ), true);

         for(int j = 0; j < achunksection.length; ++j) {
            ChunkSection chunksection = achunksection[j];
            worldlightmanager.updateSectionStatus(SectionPos.of(chunkX, j, chunkZ), ChunkSection.isEmpty(chunksection));
         }

         this.world.onChunkLoaded(chunkX, chunkZ);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
         return chunk;
      }
   }

   public void tick(BooleanSupplier hasTimeLeft) {
   }

   public void setCenter(int x, int z) {
      this.array.centerX = x;
      this.array.centerZ = z;
   }

   public void setViewDistance(int viewDistance) {
      int i = this.array.viewDistance;
      int j = adjustViewDistance(viewDistance);
      if (i != j) {
         ClientChunkProvider.ChunkArray clientchunkprovider$chunkarray = new ClientChunkProvider.ChunkArray(j);
         clientchunkprovider$chunkarray.centerX = this.array.centerX;
         clientchunkprovider$chunkarray.centerZ = this.array.centerZ;

         for(int k = 0; k < this.array.chunks.length(); ++k) {
            Chunk chunk = this.array.chunks.get(k);
            if (chunk != null) {
               ChunkPos chunkpos = chunk.getPos();
               if (clientchunkprovider$chunkarray.inView(chunkpos.x, chunkpos.z)) {
                  clientchunkprovider$chunkarray.replace(clientchunkprovider$chunkarray.getIndex(chunkpos.x, chunkpos.z), chunk);
               }
            }
         }

         this.array = clientchunkprovider$chunkarray;
      }

   }

   private static int adjustViewDistance(int p_217254_0_) {
      return Math.max(2, p_217254_0_) + 3;
   }

   /**
    * Converts the instance data to a readable string.
    */
   public String makeString() {
      return "Client Chunk Cache: " + this.array.chunks.length() + ", " + this.getLoadedChunksCount();
   }

   public int getLoadedChunksCount() {
      return this.array.loaded;
   }

   public void markLightChanged(LightType type, SectionPos pos) {
      Minecraft.getInstance().worldRenderer.markForRerender(pos.getSectionX(), pos.getSectionY(), pos.getSectionZ());
   }

   public boolean canTick(BlockPos pos) {
      return this.chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
   }

   public boolean isChunkLoaded(ChunkPos pos) {
      return this.chunkExists(pos.x, pos.z);
   }

   public boolean isChunkLoaded(Entity entityIn) {
      return this.chunkExists(MathHelper.floor(entityIn.getPosX()) >> 4, MathHelper.floor(entityIn.getPosZ()) >> 4);
   }

   @OnlyIn(Dist.CLIENT)
   final class ChunkArray {
      private final AtomicReferenceArray<Chunk> chunks;
      private final int viewDistance;
      private final int sideLength;
      private volatile int centerX;
      private volatile int centerZ;
      private int loaded;

      private ChunkArray(int viewDistanceIn) {
         this.viewDistance = viewDistanceIn;
         this.sideLength = viewDistanceIn * 2 + 1;
         this.chunks = new AtomicReferenceArray<>(this.sideLength * this.sideLength);
      }

      private int getIndex(int x, int z) {
         return Math.floorMod(z, this.sideLength) * this.sideLength + Math.floorMod(x, this.sideLength);
      }

      protected void replace(int chunkIndex, @Nullable Chunk chunkIn) {
         Chunk chunk = this.chunks.getAndSet(chunkIndex, chunkIn);
         if (chunk != null) {
            --this.loaded;
            ClientChunkProvider.this.world.onChunkUnloaded(chunk);
         }

         if (chunkIn != null) {
            ++this.loaded;
         }

      }

      protected Chunk unload(int chunkIndex, Chunk chunkIn, @Nullable Chunk replaceWith) {
         if (this.chunks.compareAndSet(chunkIndex, chunkIn, replaceWith) && replaceWith == null) {
            --this.loaded;
         }

         ClientChunkProvider.this.world.onChunkUnloaded(chunkIn);
         return chunkIn;
      }

      private boolean inView(int x, int z) {
         return Math.abs(x - this.centerX) <= this.viewDistance && Math.abs(z - this.centerZ) <= this.viewDistance;
      }

      @Nullable
      protected Chunk get(int chunkIndex) {
         return this.chunks.get(chunkIndex);
      }
   }
}