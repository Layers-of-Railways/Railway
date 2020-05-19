package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnPlayerPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private UUID uniqueId;
   private double x;
   private double y;
   private double z;
   private byte yaw;
   private byte pitch;

   public SSpawnPlayerPacket() {
   }

   public SSpawnPlayerPacket(PlayerEntity player) {
      this.entityId = player.getEntityId();
      this.uniqueId = player.getGameProfile().getId();
      this.x = player.getPosX();
      this.y = player.getPosY();
      this.z = player.getPosZ();
      this.yaw = (byte)((int)(player.rotationYaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(player.rotationPitch * 256.0F / 360.0F));
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.uniqueId = buf.readUniqueId();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readByte();
      this.pitch = buf.readByte();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeUniqueId(this.uniqueId);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeByte(this.yaw);
      buf.writeByte(this.pitch);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleSpawnPlayer(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
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
   public byte getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }
}