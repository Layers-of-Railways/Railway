package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SChunkDataPacket implements IPacket<IClientPlayNetHandler> {
   private int chunkX;
   private int chunkZ;
   private int availableSections;
   private CompoundNBT heightmapTags;
   @Nullable
   private BiomeContainer biomes;
   private byte[] buffer;
   private List<CompoundNBT> tileEntityTags;
   private boolean fullChunk;

   public SChunkDataPacket() {
   }

   public SChunkDataPacket(Chunk chunkIn, int changedSectionFilter) {
      ChunkPos chunkpos = chunkIn.getPos();
      this.chunkX = chunkpos.x;
      this.chunkZ = chunkpos.z;
      this.fullChunk = changedSectionFilter == 65535;
      this.heightmapTags = new CompoundNBT();

      for(Entry<Heightmap.Type, Heightmap> entry : chunkIn.getHeightmaps()) {
         if (entry.getKey().isUsageClient()) {
            this.heightmapTags.put(entry.getKey().getId(), new LongArrayNBT(entry.getValue().getDataArray()));
         }
      }

      if (this.fullChunk) {
         this.biomes = chunkIn.getBiomes().clone();
      }

      this.buffer = new byte[this.calculateChunkSize(chunkIn, changedSectionFilter)];
      this.availableSections = this.extractChunkData(new PacketBuffer(this.getWriteBuffer()), chunkIn, changedSectionFilter);
      this.tileEntityTags = Lists.newArrayList();

      for(Entry<BlockPos, TileEntity> entry1 : chunkIn.getTileEntityMap().entrySet()) {
         BlockPos blockpos = entry1.getKey();
         TileEntity tileentity = entry1.getValue();
         int i = blockpos.getY() >> 4;
         if (this.isFullChunk() || (changedSectionFilter & 1 << i) != 0) {
            CompoundNBT compoundnbt = tileentity.getUpdateTag();
            this.tileEntityTags.add(compoundnbt);
         }
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chunkX = buf.readInt();
      this.chunkZ = buf.readInt();
      this.fullChunk = buf.readBoolean();
      this.availableSections = buf.readVarInt();
      this.heightmapTags = buf.readCompoundTag();
      if (this.fullChunk) {
         this.biomes = new BiomeContainer(buf);
      }

      int i = buf.readVarInt();
      if (i > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.buffer = new byte[i];
         buf.readBytes(this.buffer);
         int j = buf.readVarInt();
         this.tileEntityTags = Lists.newArrayList();

         for(int k = 0; k < j; ++k) {
            this.tileEntityTags.add(buf.readCompoundTag());
         }

      }
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.chunkX);
      buf.writeInt(this.chunkZ);
      buf.writeBoolean(this.fullChunk);
      buf.writeVarInt(this.availableSections);
      buf.writeCompoundTag(this.heightmapTags);
      if (this.biomes != null) {
         this.biomes.writeToBuf(buf);
      }

      buf.writeVarInt(this.buffer.length);
      buf.writeBytes(this.buffer);
      buf.writeVarInt(this.tileEntityTags.size());

      for(CompoundNBT compoundnbt : this.tileEntityTags) {
         buf.writeCompoundTag(compoundnbt);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleChunkData(this);
   }

   @OnlyIn(Dist.CLIENT)
   public PacketBuffer getReadBuffer() {
      return new PacketBuffer(Unpooled.wrappedBuffer(this.buffer));
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
      bytebuf.writerIndex(0);
      return bytebuf;
   }

   public int extractChunkData(PacketBuffer buf, Chunk chunkIn, int writeSkylight) {
      int i = 0;
      ChunkSection[] achunksection = chunkIn.getSections();
      int j = 0;

      for(int k = achunksection.length; j < k; ++j) {
         ChunkSection chunksection = achunksection[j];
         if (chunksection != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !chunksection.isEmpty()) && (writeSkylight & 1 << j) != 0) {
            i |= 1 << j;
            chunksection.write(buf);
         }
      }

      return i;
   }

   protected int calculateChunkSize(Chunk chunkIn, int changedSectionsIn) {
      int i = 0;
      ChunkSection[] achunksection = chunkIn.getSections();
      int j = 0;

      for(int k = achunksection.length; j < k; ++j) {
         ChunkSection chunksection = achunksection[j];
         if (chunksection != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !chunksection.isEmpty()) && (changedSectionsIn & 1 << j) != 0) {
            i += chunksection.getSize();
         }
      }

      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkX() {
      return this.chunkX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkZ() {
      return this.chunkZ;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAvailableSections() {
      return this.availableSections;
   }

   public boolean isFullChunk() {
      return this.fullChunk;
   }

   @OnlyIn(Dist.CLIENT)
   public CompoundNBT getHeightmapTags() {
      return this.heightmapTags;
   }

   @OnlyIn(Dist.CLIENT)
   public List<CompoundNBT> getTileEntityTags() {
      return this.tileEntityTags;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public BiomeContainer getBiomes() {
      return this.biomes == null ? null : this.biomes.clone();
   }
}