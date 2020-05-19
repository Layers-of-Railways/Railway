package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEnableCompressionPacket implements IPacket<IClientLoginNetHandler> {
   private int compressionThreshold;

   public SEnableCompressionPacket() {
   }

   public SEnableCompressionPacket(int thresholdIn) {
      this.compressionThreshold = thresholdIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.compressionThreshold = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.compressionThreshold);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientLoginNetHandler handler) {
      handler.handleEnableCompression(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}