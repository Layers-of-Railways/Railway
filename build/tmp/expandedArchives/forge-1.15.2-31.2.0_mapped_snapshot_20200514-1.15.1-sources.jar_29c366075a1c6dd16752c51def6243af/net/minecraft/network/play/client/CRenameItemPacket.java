package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CRenameItemPacket implements IPacket<IServerPlayNetHandler> {
   private String name;

   public CRenameItemPacket() {
   }

   public CRenameItemPacket(String p_i49546_1_) {
      this.name = p_i49546_1_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.name = buf.readString(32767);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.name);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}