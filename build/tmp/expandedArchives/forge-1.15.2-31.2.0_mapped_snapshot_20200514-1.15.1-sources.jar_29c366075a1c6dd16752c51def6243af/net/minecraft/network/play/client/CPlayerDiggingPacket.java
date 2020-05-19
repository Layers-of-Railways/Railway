package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlayerDiggingPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos position;
   private Direction facing;
   /** Status of the digging (started, ongoing, broken). */
   private CPlayerDiggingPacket.Action action;

   public CPlayerDiggingPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerDiggingPacket(CPlayerDiggingPacket.Action actionIn, BlockPos posIn, Direction facingIn) {
      this.action = actionIn;
      this.position = posIn.toImmutable();
      this.facing = facingIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.action = buf.readEnumValue(CPlayerDiggingPacket.Action.class);
      this.position = buf.readBlockPos();
      this.facing = Direction.byIndex(buf.readUnsignedByte());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.action);
      buf.writeBlockPos(this.position);
      buf.writeByte(this.facing.getIndex());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processPlayerDigging(this);
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public Direction getFacing() {
      return this.facing;
   }

   public CPlayerDiggingPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      START_DESTROY_BLOCK,
      ABORT_DESTROY_BLOCK,
      STOP_DESTROY_BLOCK,
      DROP_ALL_ITEMS,
      DROP_ITEM,
      RELEASE_USE_ITEM,
      SWAP_HELD_ITEMS;
   }
}