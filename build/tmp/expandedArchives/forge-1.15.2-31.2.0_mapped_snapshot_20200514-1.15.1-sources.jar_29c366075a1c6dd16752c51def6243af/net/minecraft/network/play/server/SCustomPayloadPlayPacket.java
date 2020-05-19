package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCustomPayloadPlayPacket implements IPacket<IClientPlayNetHandler>, net.minecraftforge.fml.network.ICustomPacket<SCustomPayloadPlayPacket> {
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   public static final ResourceLocation DEBUG_PATH = new ResourceLocation("debug/path");
   public static final ResourceLocation DEBUG_NEIGHBORS_UPDATE = new ResourceLocation("debug/neighbors_update");
   public static final ResourceLocation DEBUG_CAVES = new ResourceLocation("debug/caves");
   public static final ResourceLocation DEBUG_STRUCTURES = new ResourceLocation("debug/structures");
   public static final ResourceLocation DEBUG_WORLDGEN_ATTEMPT = new ResourceLocation("debug/worldgen_attempt");
   public static final ResourceLocation DEBUG_POI_TICKET_COUNT = new ResourceLocation("debug/poi_ticket_count");
   public static final ResourceLocation DEBUG_POI_ADDED = new ResourceLocation("debug/poi_added");
   public static final ResourceLocation DEBUG_POI_REMOVED = new ResourceLocation("debug/poi_removed");
   public static final ResourceLocation DEBUG_VILLAGE_SECTIONS = new ResourceLocation("debug/village_sections");
   public static final ResourceLocation DEBUG_GOAL_SELECTOR = new ResourceLocation("debug/goal_selector");
   public static final ResourceLocation DEBUG_BRAIN = new ResourceLocation("debug/brain");
   public static final ResourceLocation field_229727_m_ = new ResourceLocation("debug/bee");
   public static final ResourceLocation field_229728_n_ = new ResourceLocation("debug/hive");
   public static final ResourceLocation field_229729_o_ = new ResourceLocation("debug/game_test_add_marker");
   public static final ResourceLocation field_229730_p_ = new ResourceLocation("debug/game_test_clear");
   public static final ResourceLocation DEBUG_RAIDS = new ResourceLocation("debug/raids");
   private ResourceLocation channel;
   private PacketBuffer data;

   public SCustomPayloadPlayPacket() {
   }

   public SCustomPayloadPlayPacket(ResourceLocation channelIn, PacketBuffer dataIn) {
      this.channel = channelIn;
      this.data = dataIn;
      if (dataIn.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.channel = buf.readResourceLocation();
      int i = buf.readableBytes();
      if (i >= 0 && i <= 1048576) {
         this.data = new PacketBuffer(buf.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeResourceLocation(this.channel);
      buf.writeBytes(this.data.copy());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleCustomPayload(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getChannelName() {
      return this.channel;
   }

   @OnlyIn(Dist.CLIENT)
   public PacketBuffer getBufferData() {
      return new PacketBuffer(this.data.copy());
   }
}