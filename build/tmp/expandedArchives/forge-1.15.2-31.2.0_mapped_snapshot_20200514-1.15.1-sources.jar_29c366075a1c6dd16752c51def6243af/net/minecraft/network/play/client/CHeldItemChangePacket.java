package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CHeldItemChangePacket implements IPacket<IServerPlayNetHandler> {
   private int slotId;

   public CHeldItemChangePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CHeldItemChangePacket(int slotIdIn) {
      this.slotId = slotIdIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.slotId = buf.readShort();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeShort(this.slotId);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processHeldItemChange(this);
   }

   public int getSlotId() {
      return this.slotId;
   }
}