package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCustomPayloadPacket implements IPacket<IServerPlayNetHandler>, net.minecraftforge.fml.network.ICustomPacket<CCustomPayloadPacket> {
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   private ResourceLocation channel;
   private PacketBuffer data;

   public CCustomPayloadPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CCustomPayloadPacket(ResourceLocation channelIn, PacketBuffer dataIn) {
      this.channel = channelIn;
      this.data = dataIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.channel = buf.readResourceLocation();
      int i = buf.readableBytes();
      if (i >= 0 && i <= 32767) {
         this.data = new PacketBuffer(buf.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeResourceLocation(this.channel);
      buf.writeBytes((ByteBuf)this.data.copy()); //This may be access multiple times, from multiple threads, lets be safe like the S->C packet
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processCustomPayload(this);
      if (this.data != null) {
         this.data.release();
      }

   }
}