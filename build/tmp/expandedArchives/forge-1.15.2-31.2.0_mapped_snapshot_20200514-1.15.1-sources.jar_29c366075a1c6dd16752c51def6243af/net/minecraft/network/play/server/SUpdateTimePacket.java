package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateTimePacket implements IPacket<IClientPlayNetHandler> {
   private long totalWorldTime;
   private long worldTime;

   public SUpdateTimePacket() {
   }

   public SUpdateTimePacket(long totalWorldTimeIn, long worldTimeIn, boolean doDaylightCycle) {
      this.totalWorldTime = totalWorldTimeIn;
      this.worldTime = worldTimeIn;
      if (!doDaylightCycle) {
         this.worldTime = -this.worldTime;
         if (this.worldTime == 0L) {
            this.worldTime = -1L;
         }
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.totalWorldTime = buf.readLong();
      this.worldTime = buf.readLong();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeLong(this.totalWorldTime);
      buf.writeLong(this.worldTime);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleTimeUpdate(this);
   }

   @OnlyIn(Dist.CLIENT)
   public long getTotalWorldTime() {
      return this.totalWorldTime;
   }

   @OnlyIn(Dist.CLIENT)
   public long getWorldTime() {
      return this.worldTime;
   }
}