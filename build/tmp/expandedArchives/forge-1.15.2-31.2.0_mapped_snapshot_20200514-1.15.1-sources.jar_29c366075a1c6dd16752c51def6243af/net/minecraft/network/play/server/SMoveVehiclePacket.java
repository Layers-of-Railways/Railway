package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMoveVehiclePacket implements IPacket<IClientPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;

   public SMoveVehiclePacket() {
   }

   public SMoveVehiclePacket(Entity entityIn) {
      this.x = entityIn.getPosX();
      this.y = entityIn.getPosY();
      this.z = entityIn.getPosZ();
      this.yaw = entityIn.rotationYaw;
      this.pitch = entityIn.rotationPitch;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleMoveVehicle(this);
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
   public float getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPitch() {
      return this.pitch;
   }
}