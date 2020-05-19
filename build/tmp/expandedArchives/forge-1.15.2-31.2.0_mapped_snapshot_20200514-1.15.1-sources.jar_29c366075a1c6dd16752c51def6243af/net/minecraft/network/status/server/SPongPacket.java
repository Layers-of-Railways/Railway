package net.minecraft.network.status.server;

import java.io.IOException;
import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SPongPacket implements IPacket<IClientStatusNetHandler> {
   private long clientTime;

   public SPongPacket() {
   }

   public SPongPacket(long clientTimeIn) {
      this.clientTime = clientTimeIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.clientTime = buf.readLong();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeLong(this.clientTime);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientStatusNetHandler handler) {
      handler.handlePong(this);
   }
}