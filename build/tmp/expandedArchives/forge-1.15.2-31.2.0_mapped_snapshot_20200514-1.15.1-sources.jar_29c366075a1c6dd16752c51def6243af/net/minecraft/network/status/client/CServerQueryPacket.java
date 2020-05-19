package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.IServerStatusNetHandler;

public class CServerQueryPacket implements IPacket<IServerStatusNetHandler> {
   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerStatusNetHandler handler) {
      handler.processServerQuery(this);
   }
}