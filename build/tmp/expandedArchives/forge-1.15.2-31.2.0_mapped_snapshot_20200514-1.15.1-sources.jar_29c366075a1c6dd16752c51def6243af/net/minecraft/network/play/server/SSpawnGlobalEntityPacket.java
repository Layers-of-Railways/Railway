package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnGlobalEntityPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private double x;
   private double y;
   private double z;
   private int type;

   public SSpawnGlobalEntityPacket() {
   }

   public SSpawnGlobalEntityPacket(Entity entityIn) {
      this.entityId = entityIn.getEntityId();
      this.x = entityIn.getPosX();
      this.y = entityIn.getPosY();
      this.z = entityIn.getPosZ();
      if (entityIn instanceof LightningBoltEntity) {
         this.type = 1;
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.type = buf.readByte();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeByte(this.type);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleSpawnGlobalEntity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public int getType() {
      return this.type;
   }
}