package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateLightPacket implements IPacket<IClientPlayNetHandler> {
   private int chunkX;
   private int chunkZ;
   private int skyLightUpdateMask;
   private int blockLightUpdateMask;
   private int skyLightResetMask;
   private int blockLightResetMask;
   private List<byte[]> skyLightData;
   private List<byte[]> blockLightData;

   public SUpdateLightPacket() {
   }

   public SUpdateLightPacket(ChunkPos pos, WorldLightManager lightManager) {
      this.chunkX = pos.x;
      this.chunkZ = pos.z;
      this.skyLightData = Lists.newArrayList();
      this.blockLightData = Lists.newArrayList();

      for(int i = 0; i < 18; ++i) {
         NibbleArray nibblearray = lightManager.getLightEngine(LightType.SKY).getData(SectionPos.from(pos, -1 + i));
         NibbleArray nibblearray1 = lightManager.getLightEngine(LightType.BLOCK).getData(SectionPos.from(pos, -1 + i));
         if (nibblearray != null) {
            if (nibblearray.isEmpty()) {
               this.skyLightResetMask |= 1 << i;
            } else {
               this.skyLightUpdateMask |= 1 << i;
               this.skyLightData.add((byte[])nibblearray.getData().clone());
            }
         }

         if (nibblearray1 != null) {
            if (nibblearray1.isEmpty()) {
               this.blockLightResetMask |= 1 << i;
            } else {
               this.blockLightUpdateMask |= 1 << i;
               this.blockLightData.add((byte[])nibblearray1.getData().clone());
            }
         }
      }

   }

   public SUpdateLightPacket(ChunkPos pos, WorldLightManager lightManager, int skyLightUpdateMaskIn, int blockLightUpdateMaskIn) {
      this.chunkX = pos.x;
      this.chunkZ = pos.z;
      this.skyLightUpdateMask = skyLightUpdateMaskIn;
      this.blockLightUpdateMask = blockLightUpdateMaskIn;
      this.skyLightData = Lists.newArrayList();
      this.blockLightData = Lists.newArrayList();

      for(int i = 0; i < 18; ++i) {
         if ((this.skyLightUpdateMask & 1 << i) != 0) {
            NibbleArray nibblearray = lightManager.getLightEngine(LightType.SKY).getData(SectionPos.from(pos, -1 + i));
            if (nibblearray != null && !nibblearray.isEmpty()) {
               this.skyLightData.add((byte[])nibblearray.getData().clone());
            } else {
               this.skyLightUpdateMask &= ~(1 << i);
               if (nibblearray != null) {
                  this.skyLightResetMask |= 1 << i;
               }
            }
         }

         if ((this.blockLightUpdateMask & 1 << i) != 0) {
            NibbleArray nibblearray1 = lightManager.getLightEngine(LightType.BLOCK).getData(SectionPos.from(pos, -1 + i));
            if (nibblearray1 != null && !nibblearray1.isEmpty()) {
               this.blockLightData.add((byte[])nibblearray1.getData().clone());
            } else {
               this.blockLightUpdateMask &= ~(1 << i);
               if (nibblearray1 != null) {
                  this.blockLightResetMask |= 1 << i;
               }
            }
         }
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chunkX = buf.readVarInt();
      this.chunkZ = buf.readVarInt();
      this.skyLightUpdateMask = buf.readVarInt();
      this.blockLightUpdateMask = buf.readVarInt();
      this.skyLightResetMask = buf.readVarInt();
      this.blockLightResetMask = buf.readVarInt();
      this.skyLightData = Lists.newArrayList();

      for(int i = 0; i < 18; ++i) {
         if ((this.skyLightUpdateMask & 1 << i) != 0) {
            this.skyLightData.add(buf.readByteArray(2048));
         }
      }

      this.blockLightData = Lists.newArrayList();

      for(int j = 0; j < 18; ++j) {
         if ((this.blockLightUpdateMask & 1 << j) != 0) {
            this.blockLightData.add(buf.readByteArray(2048));
         }
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.chunkX);
      buf.writeVarInt(this.chunkZ);
      buf.writeVarInt(this.skyLightUpdateMask);
      buf.writeVarInt(this.blockLightUpdateMask);
      buf.writeVarInt(this.skyLightResetMask);
      buf.writeVarInt(this.blockLightResetMask);

      for(byte[] abyte : this.skyLightData) {
         buf.writeByteArray(abyte);
      }

      for(byte[] abyte1 : this.blockLightData) {
         buf.writeByteArray(abyte1);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleUpdateLight(this);
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
   public int getSkyLightUpdateMask() {
      return this.skyLightUpdateMask;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyLightResetMask() {
      return this.skyLightResetMask;
   }

   @OnlyIn(Dist.CLIENT)
   public List<byte[]> getSkyLightData() {
      return this.skyLightData;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBlockLightUpdateMask() {
      return this.blockLightUpdateMask;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBlockLightResetMask() {
      return this.blockLightResetMask;
   }

   @OnlyIn(Dist.CLIENT)
   public List<byte[]> getBlockLightData() {
      return this.blockLightData;
   }
}