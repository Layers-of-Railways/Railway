package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnPaintingPacket implements IPacket<IClientPlayNetHandler> {
   private int entityID;
   private UUID uniqueId;
   private BlockPos position;
   private Direction facing;
   private int title;

   public SSpawnPaintingPacket() {
   }

   public SSpawnPaintingPacket(PaintingEntity painting) {
      this.entityID = painting.getEntityId();
      this.uniqueId = painting.getUniqueID();
      this.position = painting.getHangingPosition();
      this.facing = painting.getHorizontalFacing();
      this.title = Registry.MOTIVE.getId(painting.art);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityID = buf.readVarInt();
      this.uniqueId = buf.readUniqueId();
      this.title = buf.readVarInt();
      this.position = buf.readBlockPos();
      this.facing = Direction.byHorizontalIndex(buf.readUnsignedByte());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityID);
      buf.writeUniqueId(this.uniqueId);
      buf.writeVarInt(this.title);
      buf.writeBlockPos(this.position);
      buf.writeByte(this.facing.getHorizontalIndex());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleSpawnPainting(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPosition() {
      return this.position;
   }

   @OnlyIn(Dist.CLIENT)
   public Direction getFacing() {
      return this.facing;
   }

   @OnlyIn(Dist.CLIENT)
   public PaintingType getType() {
      return Registry.MOTIVE.getByValue(this.title);
   }
}