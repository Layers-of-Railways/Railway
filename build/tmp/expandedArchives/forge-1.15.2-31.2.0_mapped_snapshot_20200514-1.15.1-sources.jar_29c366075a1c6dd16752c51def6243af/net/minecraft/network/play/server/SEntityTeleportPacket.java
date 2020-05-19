package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityTeleportPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private double posX;
   private double posY;
   private double posZ;
   private byte yaw;
   private byte pitch;
   private boolean onGround;

   public SEntityTeleportPacket() {
   }

   public SEntityTeleportPacket(Entity entityIn) {
      this.entityId = entityIn.getEntityId();
      this.posX = entityIn.getPosX();
      this.posY = entityIn.getPosY();
      this.posZ = entityIn.getPosZ();
      this.yaw = (byte)((int)(entityIn.rotationYaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(entityIn.rotationPitch * 256.0F / 360.0F));
      this.onGround = entityIn.onGround;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.posX = buf.readDouble();
      this.posY = buf.readDouble();
      this.posZ = buf.readDouble();
      this.yaw = buf.readByte();
      this.pitch = buf.readByte();
      this.onGround = buf.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
      buf.writeByte(this.yaw);
      buf.writeByte(this.pitch);
      buf.writeBoolean(this.onGround);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleEntityTeleport(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.posZ;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOnGround() {
      return this.onGround;
   }
}