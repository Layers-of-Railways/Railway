package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CQueryEntityNBTPacket implements IPacket<IServerPlayNetHandler> {
   private int transactionId;
   private int entityId;

   public CQueryEntityNBTPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CQueryEntityNBTPacket(int p_i49755_1_, int p_i49755_2_) {
      this.transactionId = p_i49755_1_;
      this.entityId = p_i49755_2_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.transactionId = buf.readVarInt();
      this.entityId = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.transactionId);
      buf.writeVarInt(this.entityId);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processNBTQueryEntity(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public int getEntityId() {
      return this.entityId;
   }
}