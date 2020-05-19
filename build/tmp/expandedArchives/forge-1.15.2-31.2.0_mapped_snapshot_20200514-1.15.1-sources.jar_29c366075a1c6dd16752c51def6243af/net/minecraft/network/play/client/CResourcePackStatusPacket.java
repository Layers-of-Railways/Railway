package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CResourcePackStatusPacket implements IPacket<IServerPlayNetHandler> {
   private CResourcePackStatusPacket.Action action;

   public CResourcePackStatusPacket() {
   }

   public CResourcePackStatusPacket(CResourcePackStatusPacket.Action p_i47156_1_) {
      this.action = p_i47156_1_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.action = buf.readEnumValue(CResourcePackStatusPacket.Action.class);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.action);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.handleResourcePackStatus(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;
   }
}