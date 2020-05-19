package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateSignPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private String[] lines;

   public CUpdateSignPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateSignPacket(BlockPos p_i49822_1_, ITextComponent p_i49822_2_, ITextComponent p_i49822_3_, ITextComponent p_i49822_4_, ITextComponent p_i49822_5_) {
      this.pos = p_i49822_1_;
      this.lines = new String[]{p_i49822_2_.getString(), p_i49822_3_.getString(), p_i49822_4_.getString(), p_i49822_5_.getString()};
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.pos = buf.readBlockPos();
      this.lines = new String[4];

      for(int i = 0; i < 4; ++i) {
         this.lines[i] = buf.readString(384);
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.pos);

      for(int i = 0; i < 4; ++i) {
         buf.writeString(this.lines[i]);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processUpdateSign(this);
   }

   public BlockPos getPosition() {
      return this.pos;
   }

   public String[] getLines() {
      return this.lines;
   }
}