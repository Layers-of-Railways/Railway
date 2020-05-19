package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMultiBlockChangePacket implements IPacket<IClientPlayNetHandler> {
   private ChunkPos chunkPos;
   private SMultiBlockChangePacket.UpdateData[] changedBlocks;

   public SMultiBlockChangePacket() {
   }

   public SMultiBlockChangePacket(int p_i46959_1_, short[] p_i46959_2_, Chunk p_i46959_3_) {
      this.chunkPos = p_i46959_3_.getPos();
      this.changedBlocks = new SMultiBlockChangePacket.UpdateData[p_i46959_1_];

      for(int i = 0; i < this.changedBlocks.length; ++i) {
         this.changedBlocks[i] = new SMultiBlockChangePacket.UpdateData(p_i46959_2_[i], p_i46959_3_);
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chunkPos = new ChunkPos(buf.readInt(), buf.readInt());
      this.changedBlocks = new SMultiBlockChangePacket.UpdateData[buf.readVarInt()];

      for(int i = 0; i < this.changedBlocks.length; ++i) {
         this.changedBlocks[i] = new SMultiBlockChangePacket.UpdateData(buf.readShort(), Block.BLOCK_STATE_IDS.getByValue(buf.readVarInt()));
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.chunkPos.x);
      buf.writeInt(this.chunkPos.z);
      buf.writeVarInt(this.changedBlocks.length);

      for(SMultiBlockChangePacket.UpdateData smultiblockchangepacket$updatedata : this.changedBlocks) {
         buf.writeShort(smultiblockchangepacket$updatedata.getOffset());
         buf.writeVarInt(Block.getStateId(smultiblockchangepacket$updatedata.getBlockState()));
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleMultiBlockChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public SMultiBlockChangePacket.UpdateData[] getChangedBlocks() {
      return this.changedBlocks;
   }

   public class UpdateData {
      /** contains the bitshifted location of the block in the chunk */
      private final short offset;
      private final BlockState blockState;

      public UpdateData(short p_i46544_2_, BlockState p_i46544_3_) {
         this.offset = p_i46544_2_;
         this.blockState = p_i46544_3_;
      }

      public UpdateData(short p_i46545_2_, Chunk p_i46545_3_) {
         this.offset = p_i46545_2_;
         this.blockState = p_i46545_3_.getBlockState(this.getPos());
      }

      public BlockPos getPos() {
         return new BlockPos(SMultiBlockChangePacket.this.chunkPos.getBlock(this.offset >> 12 & 15, this.offset & 255, this.offset >> 8 & 15));
      }

      public short getOffset() {
         return this.offset;
      }

      public BlockState getBlockState() {
         return this.blockState;
      }
   }
}