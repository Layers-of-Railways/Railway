package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CConfirmTeleportPacket implements IPacket<IServerPlayNetHandler> {
   private int telportId;

   public CConfirmTeleportPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CConfirmTeleportPacket(int teleportIdIn) {
      this.telportId = teleportIdIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.telportId = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.telportId);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processConfirmTeleport(this);
   }

   public int getTeleportId() {
      return this.telportId;
   }
}