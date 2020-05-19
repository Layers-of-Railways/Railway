package net.minecraft.network.login.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.IServerLoginNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCustomPayloadLoginPacket implements IPacket<IServerLoginNetHandler>, net.minecraftforge.fml.network.ICustomPacket<CCustomPayloadLoginPacket> {
   private int transaction;
   private PacketBuffer payload;

   public CCustomPayloadLoginPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CCustomPayloadLoginPacket(int p_i49516_1_, @Nullable PacketBuffer p_i49516_2_) {
      this.transaction = p_i49516_1_;
      this.payload = p_i49516_2_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.transaction = buf.readVarInt();
      if (buf.readBoolean()) {
         int i = buf.readableBytes();
         if (i < 0 || i > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
         }

         this.payload = new PacketBuffer(buf.readBytes(i));
      } else {
         this.payload = null;
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.transaction);
      if (this.payload != null) {
         buf.writeBoolean(true);
         buf.writeBytes(this.payload.copy());
      } else {
         buf.writeBoolean(false);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerLoginNetHandler handler) {
      handler.processCustomPayloadLogin(this);
   }
}