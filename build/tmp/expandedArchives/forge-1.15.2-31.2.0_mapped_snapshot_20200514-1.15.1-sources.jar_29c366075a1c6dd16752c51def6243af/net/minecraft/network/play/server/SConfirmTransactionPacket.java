package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SConfirmTransactionPacket implements IPacket<IClientPlayNetHandler> {
   private int windowId;
   private short actionNumber;
   private boolean accepted;

   public SConfirmTransactionPacket() {
   }

   public SConfirmTransactionPacket(int windowIdIn, short actionNumberIn, boolean acceptedIn) {
      this.windowId = windowIdIn;
      this.actionNumber = actionNumberIn;
      this.accepted = acceptedIn;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleConfirmTransaction(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readUnsignedByte();
      this.actionNumber = buf.readShort();
      this.accepted = buf.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.actionNumber);
      buf.writeBoolean(this.accepted);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @OnlyIn(Dist.CLIENT)
   public short getActionNumber() {
      return this.actionNumber;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean wasAccepted() {
      return this.accepted;
   }
}