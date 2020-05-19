package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlayerPacket implements IPacket<IServerPlayNetHandler> {
   protected double x;
   protected double y;
   protected double z;
   protected float yaw;
   protected float pitch;
   protected boolean onGround;
   protected boolean moving;
   protected boolean rotating;

   public CPlayerPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerPacket(boolean onGroundIn) {
      this.onGround = onGroundIn;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processPlayer(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.onGround = buf.readUnsignedByte() != 0;
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.onGround ? 1 : 0);
   }

   public double getX(double defaultValue) {
      return this.moving ? this.x : defaultValue;
   }

   public double getY(double defaultValue) {
      return this.moving ? this.y : defaultValue;
   }

   public double getZ(double defaultValue) {
      return this.moving ? this.z : defaultValue;
   }

   public float getYaw(float defaultValue) {
      return this.rotating ? this.yaw : defaultValue;
   }

   public float getPitch(float defaultValue) {
      return this.rotating ? this.pitch : defaultValue;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public static class PositionPacket extends CPlayerPacket {
      public PositionPacket() {
         this.moving = true;
      }

      @OnlyIn(Dist.CLIENT)
      public PositionPacket(double xIn, double yIn, double zIn, boolean onGroundIn) {
         this.x = xIn;
         this.y = yIn;
         this.z = zIn;
         this.onGround = onGroundIn;
         this.moving = true;
      }

      /**
       * Reads the raw packet data from the data stream.
       */
      public void readPacketData(PacketBuffer buf) throws IOException {
         this.x = buf.readDouble();
         this.y = buf.readDouble();
         this.z = buf.readDouble();
         super.readPacketData(buf);
      }

      /**
       * Writes the raw packet data to the data stream.
       */
      public void writePacketData(PacketBuffer buf) throws IOException {
         buf.writeDouble(this.x);
         buf.writeDouble(this.y);
         buf.writeDouble(this.z);
         super.writePacketData(buf);
      }
   }

   public static class PositionRotationPacket extends CPlayerPacket {
      public PositionRotationPacket() {
         this.moving = true;
         this.rotating = true;
      }

      @OnlyIn(Dist.CLIENT)
      public PositionRotationPacket(double xIn, double yIn, double zIn, float yawIn, float pitchIn, boolean onGroundIn) {
         this.x = xIn;
         this.y = yIn;
         this.z = zIn;
         this.yaw = yawIn;
         this.pitch = pitchIn;
         this.onGround = onGroundIn;
         this.rotating = true;
         this.moving = true;
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
         super.readPacketData(buf);
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
         super.writePacketData(buf);
      }
   }

   public static class RotationPacket extends CPlayerPacket {
      public RotationPacket() {
         this.rotating = true;
      }

      @OnlyIn(Dist.CLIENT)
      public RotationPacket(float yawIn, float pitchIn, boolean onGroundIn) {
         this.yaw = yawIn;
         this.pitch = pitchIn;
         this.onGround = onGroundIn;
         this.rotating = true;
      }

      /**
       * Reads the raw packet data from the data stream.
       */
      public void readPacketData(PacketBuffer buf) throws IOException {
         this.yaw = buf.readFloat();
         this.pitch = buf.readFloat();
         super.readPacketData(buf);
      }

      /**
       * Writes the raw packet data to the data stream.
       */
      public void writePacketData(PacketBuffer buf) throws IOException {
         buf.writeFloat(this.yaw);
         buf.writeFloat(this.pitch);
         super.writePacketData(buf);
      }
   }
}