package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnPositionPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos spawnBlockPos;

   public SSpawnPositionPacket() {
   }

   public SSpawnPositionPacket(BlockPos posIn) {
      this.spawnBlockPos = posIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.spawnBlockPos = buf.readBlockPos();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.spawnBlockPos);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleSpawnPosition(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getSpawnPos() {
      return this.spawnBlockPos;
   }
}