package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenHorseWindowPacket implements IPacket<IClientPlayNetHandler> {
   private int field_218705_a;
   private int field_218706_b;
   private int field_218707_c;

   public SOpenHorseWindowPacket() {
   }

   public SOpenHorseWindowPacket(int p_i50776_1_, int p_i50776_2_, int p_i50776_3_) {
      this.field_218705_a = p_i50776_1_;
      this.field_218706_b = p_i50776_2_;
      this.field_218707_c = p_i50776_3_;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleOpenHorseWindow(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.field_218705_a = buf.readUnsignedByte();
      this.field_218706_b = buf.readVarInt();
      this.field_218707_c = buf.readInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.field_218705_a);
      buf.writeVarInt(this.field_218706_b);
      buf.writeInt(this.field_218707_c);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218704_b() {
      return this.field_218705_a;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218702_c() {
      return this.field_218706_b;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218703_d() {
      return this.field_218707_c;
   }
}