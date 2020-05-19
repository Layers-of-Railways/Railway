package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayerLookPacket implements IPacket<IClientPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private int entityId;
   private EntityAnchorArgument.Type sourceAnchor;
   private EntityAnchorArgument.Type targetAnchor;
   private boolean isEntity;

   public SPlayerLookPacket() {
   }

   public SPlayerLookPacket(EntityAnchorArgument.Type p_i48589_1_, double p_i48589_2_, double p_i48589_4_, double p_i48589_6_) {
      this.sourceAnchor = p_i48589_1_;
      this.x = p_i48589_2_;
      this.y = p_i48589_4_;
      this.z = p_i48589_6_;
   }

   public SPlayerLookPacket(EntityAnchorArgument.Type p_i48590_1_, Entity p_i48590_2_, EntityAnchorArgument.Type p_i48590_3_) {
      this.sourceAnchor = p_i48590_1_;
      this.entityId = p_i48590_2_.getEntityId();
      this.targetAnchor = p_i48590_3_;
      Vec3d vec3d = p_i48590_3_.apply(p_i48590_2_);
      this.x = vec3d.x;
      this.y = vec3d.y;
      this.z = vec3d.z;
      this.isEntity = true;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.sourceAnchor = buf.readEnumValue(EntityAnchorArgument.Type.class);
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      if (buf.readBoolean()) {
         this.isEntity = true;
         this.entityId = buf.readVarInt();
         this.targetAnchor = buf.readEnumValue(EntityAnchorArgument.Type.class);
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.sourceAnchor);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeBoolean(this.isEntity);
      if (this.isEntity) {
         buf.writeVarInt(this.entityId);
         buf.writeEnumValue(this.targetAnchor);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handlePlayerLook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityAnchorArgument.Type getSourceAnchor() {
      return this.sourceAnchor;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vec3d getTargetPosition(World p_200531_1_) {
      if (this.isEntity) {
         Entity entity = p_200531_1_.getEntityByID(this.entityId);
         return entity == null ? new Vec3d(this.x, this.y, this.z) : this.targetAnchor.apply(entity);
      } else {
         return new Vec3d(this.x, this.y, this.z);
      }
   }
}