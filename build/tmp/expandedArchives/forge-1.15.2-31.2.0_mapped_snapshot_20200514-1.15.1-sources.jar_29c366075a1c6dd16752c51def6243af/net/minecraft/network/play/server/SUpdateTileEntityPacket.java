package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateTileEntityPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos blockPos;
   /** Used only for vanilla tile entities */
   private int tileEntityType;
   private CompoundNBT nbt;

   public SUpdateTileEntityPacket() {
   }

   public SUpdateTileEntityPacket(BlockPos blockPosIn, int tileEntityTypeIn, CompoundNBT compoundIn) {
      this.blockPos = blockPosIn;
      this.tileEntityType = tileEntityTypeIn;
      this.nbt = compoundIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.blockPos = buf.readBlockPos();
      this.tileEntityType = buf.readUnsignedByte();
      this.nbt = buf.readCompoundTag();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.blockPos);
      buf.writeByte((byte)this.tileEntityType);
      buf.writeCompoundTag(this.nbt);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleUpdateTileEntity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.blockPos;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTileEntityType() {
      return this.tileEntityType;
   }

   @OnlyIn(Dist.CLIENT)
   public CompoundNBT getNbtCompound() {
      return this.nbt;
   }
}