package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CChatMessagePacket implements IPacket<IServerPlayNetHandler> {
   private String message;

   public CChatMessagePacket() {
   }

   public CChatMessagePacket(String messageIn) {
      if (messageIn.length() > 256) {
         messageIn = messageIn.substring(0, 256);
      }

      this.message = messageIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.message = buf.readString(256);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.message);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processChatMessage(this);
   }

   public String getMessage() {
      return this.message;
   }
}