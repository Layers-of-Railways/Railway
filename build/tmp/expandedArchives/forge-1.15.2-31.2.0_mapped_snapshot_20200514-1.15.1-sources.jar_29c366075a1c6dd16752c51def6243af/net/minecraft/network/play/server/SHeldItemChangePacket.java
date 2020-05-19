package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SHeldItemChangePacket implements IPacket<IClientPlayNetHandler> {
   private int heldItemHotbarIndex;

   public SHeldItemChangePacket() {
   }

   public SHeldItemChangePacket(int hotbarIndexIn) {
      this.heldItemHotbarIndex = hotbarIndexIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.heldItemHotbarIndex = buf.readByte();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.heldItemHotbarIndex);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleHeldItemChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getHeldItemHotbarIndex() {
      return this.heldItemHotbarIndex;
   }
}