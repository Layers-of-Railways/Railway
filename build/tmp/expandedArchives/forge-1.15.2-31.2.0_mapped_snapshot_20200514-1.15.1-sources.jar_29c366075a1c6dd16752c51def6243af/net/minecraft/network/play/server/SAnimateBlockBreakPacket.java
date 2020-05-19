package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SAnimateBlockBreakPacket implements IPacket<IClientPlayNetHandler> {
   private int breakerId;
   private BlockPos position;
   private int progress;

   public SAnimateBlockBreakPacket() {
   }

   public SAnimateBlockBreakPacket(int breakerIdIn, BlockPos positionIn, int progressIn) {
      this.breakerId = breakerIdIn;
      this.position = positionIn;
      this.progress = progressIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.breakerId = buf.readVarInt();
      this.position = buf.readBlockPos();
      this.progress = buf.readUnsignedByte();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.breakerId);
      buf.writeBlockPos(this.position);
      buf.writeByte(this.progress);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleBlockBreakAnim(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getBreakerId() {
      return this.breakerId;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPosition() {
      return this.position;
   }

   @OnlyIn(Dist.CLIENT)
   public int getProgress() {
      return this.progress;
   }
}