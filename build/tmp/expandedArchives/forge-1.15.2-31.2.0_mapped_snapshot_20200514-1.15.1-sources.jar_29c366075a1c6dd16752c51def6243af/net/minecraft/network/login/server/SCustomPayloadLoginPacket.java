package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCustomPayloadLoginPacket implements IPacket<IClientLoginNetHandler>, net.minecraftforge.fml.network.ICustomPacket<SCustomPayloadLoginPacket> {
   private int transaction;
   private ResourceLocation channel;
   private PacketBuffer payload;

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.transaction = buf.readVarInt();
      this.channel = buf.readResourceLocation();
      int i = buf.readableBytes();
      if (i >= 0 && i <= 1048576) {
         this.payload = new PacketBuffer(buf.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.transaction);
      buf.writeResourceLocation(this.channel);
      buf.writeBytes(this.payload.copy());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientLoginNetHandler handler) {
      handler.handleCustomPayloadLogin(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTransaction() {
      return this.transaction;
   }
}