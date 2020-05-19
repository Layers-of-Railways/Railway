package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CKeepAlivePacket implements IPacket<IServerPlayNetHandler> {
   private long key;

   public CKeepAlivePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CKeepAlivePacket(long idIn) {
      this.key = idIn;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processKeepAlive(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.key = buf.readLong();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeLong(this.key);
   }

   public long getKey() {
      return this.key;
   }
}