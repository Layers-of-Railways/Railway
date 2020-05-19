package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SQueryNBTResponsePacket implements IPacket<IClientPlayNetHandler> {
   private int transactionId;
   @Nullable
   private CompoundNBT tag;

   public SQueryNBTResponsePacket() {
   }

   public SQueryNBTResponsePacket(int p_i49757_1_, @Nullable CompoundNBT p_i49757_2_) {
      this.transactionId = p_i49757_1_;
      this.tag = p_i49757_2_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.transactionId = buf.readVarInt();
      this.tag = buf.readCompoundTag();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.transactionId);
      buf.writeCompoundTag(this.tag);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleNBTQueryResponse(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public CompoundNBT getTag() {
      return this.tag;
   }

   public boolean shouldSkipErrors() {
      return true;
   }
}