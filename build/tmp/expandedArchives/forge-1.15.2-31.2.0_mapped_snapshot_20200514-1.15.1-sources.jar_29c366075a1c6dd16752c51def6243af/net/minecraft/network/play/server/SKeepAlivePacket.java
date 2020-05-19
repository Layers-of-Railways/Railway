package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SKeepAlivePacket implements IPacket<IClientPlayNetHandler> {
   private long id;

   public SKeepAlivePacket() {
   }

   public SKeepAlivePacket(long idIn) {
      this.id = idIn;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleKeepAlive(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.id = buf.readLong();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeLong(this.id);
   }

   @OnlyIn(Dist.CLIENT)
   public long getId() {
      return this.id;
   }
}