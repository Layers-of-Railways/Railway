package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUnloadChunkPacket implements IPacket<IClientPlayNetHandler> {
   private int x;
   private int z;

   public SUnloadChunkPacket() {
   }

   public SUnloadChunkPacket(int xIn, int zIn) {
      this.x = xIn;
      this.z = zIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.x = buf.readInt();
      this.z = buf.readInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.x);
      buf.writeInt(this.z);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.processChunkUnload(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public int getZ() {
      return this.z;
   }
}