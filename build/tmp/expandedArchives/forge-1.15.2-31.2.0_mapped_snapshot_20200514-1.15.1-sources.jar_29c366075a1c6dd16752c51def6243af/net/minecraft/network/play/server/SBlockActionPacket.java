package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SBlockActionPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos blockPosition;
   private int instrument;
   private int pitch;
   private Block block;

   public SBlockActionPacket() {
   }

   public SBlockActionPacket(BlockPos pos, Block blockIn, int instrumentIn, int pitchIn) {
      this.blockPosition = pos;
      this.block = blockIn;
      this.instrument = instrumentIn;
      this.pitch = pitchIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.blockPosition = buf.readBlockPos();
      this.instrument = buf.readUnsignedByte();
      this.pitch = buf.readUnsignedByte();
      this.block = Registry.BLOCK.getByValue(buf.readVarInt());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.blockPosition);
      buf.writeByte(this.instrument);
      buf.writeByte(this.pitch);
      buf.writeVarInt(Registry.BLOCK.getId(this.block));
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleBlockAction(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   /**
    * instrument data for noteblocks
    */
   @OnlyIn(Dist.CLIENT)
   public int getData1() {
      return this.instrument;
   }

   /**
    * pitch data for noteblocks
    */
   @OnlyIn(Dist.CLIENT)
   public int getData2() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public Block getBlockType() {
      return this.block;
   }
}