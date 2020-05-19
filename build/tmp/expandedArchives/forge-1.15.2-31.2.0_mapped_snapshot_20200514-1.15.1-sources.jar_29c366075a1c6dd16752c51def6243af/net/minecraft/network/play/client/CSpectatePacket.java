package net.minecraft.network.play.client;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.world.server.ServerWorld;

public class CSpectatePacket implements IPacket<IServerPlayNetHandler> {
   private UUID id;

   public CSpectatePacket() {
   }

   public CSpectatePacket(UUID uniqueIdIn) {
      this.id = uniqueIdIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.id = buf.readUniqueId();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeUniqueId(this.id);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.handleSpectate(this);
   }

   @Nullable
   public Entity getEntity(ServerWorld worldIn) {
      return worldIn.getEntityByUuid(this.id);
   }
}