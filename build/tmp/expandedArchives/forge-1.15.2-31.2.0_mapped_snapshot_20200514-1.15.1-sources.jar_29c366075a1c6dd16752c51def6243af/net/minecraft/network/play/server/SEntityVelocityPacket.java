package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityVelocityPacket implements IPacket<IClientPlayNetHandler> {
   private int entityID;
   private int motionX;
   private int motionY;
   private int motionZ;

   public SEntityVelocityPacket() {
   }

   public SEntityVelocityPacket(Entity entityIn) {
      this(entityIn.getEntityId(), entityIn.getMotion());
   }

   public SEntityVelocityPacket(int p_i50764_1_, Vec3d p_i50764_2_) {
      this.entityID = p_i50764_1_;
      double d0 = 3.9D;
      double d1 = MathHelper.clamp(p_i50764_2_.x, -3.9D, 3.9D);
      double d2 = MathHelper.clamp(p_i50764_2_.y, -3.9D, 3.9D);
      double d3 = MathHelper.clamp(p_i50764_2_.z, -3.9D, 3.9D);
      this.motionX = (int)(d1 * 8000.0D);
      this.motionY = (int)(d2 * 8000.0D);
      this.motionZ = (int)(d3 * 8000.0D);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityID = buf.readVarInt();
      this.motionX = buf.readShort();
      this.motionY = buf.readShort();
      this.motionZ = buf.readShort();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityID);
      buf.writeShort(this.motionX);
      buf.writeShort(this.motionY);
      buf.writeShort(this.motionZ);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleEntityVelocity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionX() {
      return this.motionX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionY() {
      return this.motionY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionZ() {
      return this.motionZ;
   }
}