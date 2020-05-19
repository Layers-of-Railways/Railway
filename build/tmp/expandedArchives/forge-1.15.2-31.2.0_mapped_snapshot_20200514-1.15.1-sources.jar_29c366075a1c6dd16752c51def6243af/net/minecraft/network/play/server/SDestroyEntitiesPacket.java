package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SDestroyEntitiesPacket implements IPacket<IClientPlayNetHandler> {
   private int[] entityIDs;

   public SDestroyEntitiesPacket() {
   }

   public SDestroyEntitiesPacket(int... entityIdsIn) {
      this.entityIDs = entityIdsIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityIDs = new int[buf.readVarInt()];

      for(int i = 0; i < this.entityIDs.length; ++i) {
         this.entityIDs[i] = buf.readVarInt();
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityIDs.length);

      for(int i : this.entityIDs) {
         buf.writeVarInt(i);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleDestroyEntities(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int[] getEntityIDs() {
      return this.entityIDs;
   }
}