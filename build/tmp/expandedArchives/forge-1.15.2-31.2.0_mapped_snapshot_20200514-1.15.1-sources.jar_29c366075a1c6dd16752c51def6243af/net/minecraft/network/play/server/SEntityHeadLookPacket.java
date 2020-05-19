package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityHeadLookPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private byte yaw;

   public SEntityHeadLookPacket() {
   }

   public SEntityHeadLookPacket(Entity entityIn, byte yawIn) {
      this.entityId = entityIn.getEntityId();
      this.yaw = yawIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.yaw = buf.readByte();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeByte(this.yaw);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleEntityHeadLook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }
}