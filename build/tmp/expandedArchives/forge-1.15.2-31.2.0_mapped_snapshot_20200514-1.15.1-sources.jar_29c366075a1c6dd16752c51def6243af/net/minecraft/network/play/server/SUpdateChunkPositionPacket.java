package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateChunkPositionPacket implements IPacket<IClientPlayNetHandler> {
   private int field_218756_a;
   private int field_218757_b;

   public SUpdateChunkPositionPacket() {
   }

   public SUpdateChunkPositionPacket(int p_i50766_1_, int p_i50766_2_) {
      this.field_218756_a = p_i50766_1_;
      this.field_218757_b = p_i50766_2_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.field_218756_a = buf.readVarInt();
      this.field_218757_b = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.field_218756_a);
      buf.writeVarInt(this.field_218757_b);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleChunkPositionPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218755_b() {
      return this.field_218756_a;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218754_c() {
      return this.field_218757_b;
   }
}