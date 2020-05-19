package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.world.Difficulty;

public class CSetDifficultyPacket implements IPacket<IServerPlayNetHandler> {
   private Difficulty field_218774_a;

   public CSetDifficultyPacket() {
   }

   public CSetDifficultyPacket(Difficulty p_i50762_1_) {
      this.field_218774_a = p_i50762_1_;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.func_217263_a(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.field_218774_a = Difficulty.byId(buf.readUnsignedByte());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.field_218774_a.getId());
   }

   public Difficulty func_218773_b() {
      return this.field_218774_a;
   }
}